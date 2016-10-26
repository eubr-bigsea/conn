package es.bsc.conn.jclouds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Charsets.UTF_8;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.options.TemplateOptions;

import com.google.common.io.Files;
import com.google.common.primitives.Ints;

import es.bsc.conn.Connector;
import es.bsc.conn.clients.exceptions.ConnClientException;
import es.bsc.conn.clients.jclouds.JCloudsClient;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;


public class JClouds extends Connector {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.JCLOUDS);

    private static final int MS_TO_S = 1_000;
    private static final int MB_TO_GB = 1_024;
    private static final long POLLING_INTERVAL = 5;
    private static final int TIMEOUT = 1_800;
    private static final int DEFAULT_IP_INDEX = 0;
    private int ipIndex = DEFAULT_IP_INDEX;
    
    private static final String COMPSS_APP_NAME_PROPERTY = "IT_APP_NAME";
    private static final String DEFAULT_APP_NAME = "default-app";
    private static final String APP_NAME = System.getProperty(COMPSS_APP_NAME_PROPERTY).isEmpty() ?
            DEFAULT_APP_NAME : System.getProperty(COMPSS_APP_NAME_PROPERTY);
    
    private static final HashMap<String, HardwareDescription> VMID_TO_HARDWARE_REQUEST = new HashMap<>();
    private static final HashMap<String, SoftwareDescription> VMID_TO_SOFTWARE_REQUEST = new HashMap<>();
    
    private final JCloudsClient jcloudsClient;
    private final String provider;
    private final String server;
    private final String user;
    private final String credential;
    private final String keyPairLocation;
    private final String keyPairName;
    private final long timeSlot;    

    /**
     * Initializes the JClouds connector with the given properties
     * 
     * @param props
     */
    public JClouds(Map<String, String> props) throws ConnException {
        super(props);
        
        server = props.get("Server");

        provider = props.get("provider");
        if (provider == null) {
            throw new ConnException("Provider must be specified with \"provider\" property");
        }
        
        user = props.get("provider-user");
        if (user == null) {
            throw new ConnException("Provider user must be specified with \"provider-user\" property");
        }
        
        credential = props.get("provider-user-credential");
        if (credential == null) {
            throw new ConnException("Provider user credential must be specified with \"provider-user-credential\" property");
        }
        
        String time = props.get("time-slot");
        if (time != null) {
            timeSlot = Integer.parseInt(time) * MS_TO_S;
        } else {
            throw new ConnException("Provider billing time-slot must be specified with \"time-slot\" property");
        }
        
        String index = props.get("ip-index");
        if (index != null) {
            ipIndex = Integer.parseInt(index);
        }
        
        keyPairName = props.get("vm-keypair-name");
        if (keyPairName == null) {
            throw new ConnException("Provider keypair name must be specified with \"vm-keypair-name\" property");
        }
        
        keyPairLocation = props.get("vm-keypair-location");
        if (keyPairLocation == null) {
            throw new ConnException("Provider keypair location must be specified with \"vm-keypair-location\" property");
        }

        try {
            jcloudsClient = new JCloudsClient(user, credential, provider, server);
        } catch (ConnClientException cce) {
            throw new ConnException("Exception creating client", cce);
        }
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        try {
            Template template = generateTemplate(hd, prop);
            Set<? extends NodeMetadata> vms = jcloudsClient.createVMS(APP_NAME, 1, template);
            
            String vmId = vms.iterator().next().getId();
            VMID_TO_HARDWARE_REQUEST.put(vmId, hd);
            VMID_TO_SOFTWARE_REQUEST.put(vmId, sd);
            
            return vmId;
        } catch (RunNodesException | IOException e) {
            throw new ConnException(e);
        }
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        String vmId = (String) id;
        NodeMetadata vmNodeMetadata = jcloudsClient.getNode(vmId);
        try {
            LOGGER.info("VM State is " + vmNodeMetadata.getStatus().toString());
            int tries = 0;
            while (vmNodeMetadata.getStatus() == null || !vmNodeMetadata.getStatus().equals(NodeMetadata.Status.RUNNING)) {
                if (vmNodeMetadata.getStatus().equals(NodeMetadata.Status.ERROR)) {
                    LOGGER.error("Error waiting for VM Creation. Middleware has return an error state");
                    throw new ConnException("Error waiting for VM Creation. Middleware has return an error state");
                } else if (vmNodeMetadata.getStatus().equals(NodeMetadata.Status.SUSPENDED)) {
                    LOGGER.error("VM Creation Suspended");
                    throw new ConnException("VM creation suspended");
                }
                if (tries * POLLING_INTERVAL > TIMEOUT) {
                    throw new ConnException("Maximum VM creation time reached.");
                }

                tries++;
                try {
                    Thread.sleep(POLLING_INTERVAL * MS_TO_S);
                } catch (InterruptedException ie) {
                    throw new ConnException(ie);
                }
                vmNodeMetadata = jcloudsClient.getNode(vmId);
            }
            String ip = getIp(vmNodeMetadata);
            
            // Create Virtual Resource
            VirtualResource vr = new VirtualResource();
            vr.setId(vmId);
            vr.setIp(ip);
            vr.setProperties(null);

            HardwareDescription hd = VMID_TO_HARDWARE_REQUEST.get(vmId);
            if (hd == null) {
                throw new ConnException("Unregistered hardware description for vmId = " + vmId);
            }
            
            List<es.bsc.conn.types.Processor> procs = new ArrayList<>();
            int totalCores = 0;
            for (Processor p : vmNodeMetadata.getHardware().getProcessors()) {
                es.bsc.conn.types.Processor runtimeProc = new es.bsc.conn.types.Processor();
                int pCores = (int) p.getCores();
                runtimeProc.setComputingUnits((int) p.getCores());
                runtimeProc.setSpeed((float) p.getSpeed());
                procs.add(runtimeProc);
                totalCores = totalCores + pCores;
            }
            hd.setProcessors(procs);
            hd.setTotalComputingUnits(totalCores);
            hd.setMemorySize(vmNodeMetadata.getHardware().getRam() / Float.valueOf(MB_TO_GB));
            float disk = getTotalDisk(vmNodeMetadata.getHardware().getVolumes());
            hd.setStorageSize(disk);
            vr.setHd(hd);

            SoftwareDescription sd = VMID_TO_SOFTWARE_REQUEST.get(vmId);
            if (sd == null) {
                throw new ConnException("Unregistered software description for vmId = " + vmId);
            }
            sd.setOperatingSystemType("Linux");
            vr.setSd(sd);

            return vr;
        } catch (Exception e) {
            LOGGER.error("Exception waiting for VM Creation");
            throw new ConnException("Exception waiting for VM Creation", e);
        }
    }

    @Override
    public void destroy(Object id) {
        String vmId = (String) id;
        
        jcloudsClient.destroyNode(vmId);
        VMID_TO_HARDWARE_REQUEST.remove(vmId);
        VMID_TO_SOFTWARE_REQUEST.remove(vmId);
    }

    @Override
    public long getTimeSlot() {
        return timeSlot;
    }
    
    @Override
    public float getPriceSlot(VirtualResource virtualResource) {
        return virtualResource.getHd().getPricePerUnit();
    }

    @Override
    public void close() {
        // Nothing to do
    }

    private Template generateTemplate(HardwareDescription hd, Map<String, String> prop) throws IOException {
        TemplateOptions to = new TemplateOptions();
        
        String key = keyPairLocation + keyPairName;
        LOGGER.debug("Authorizing keys :" + key);
        to.authorizePublicKey(Files.toString(new File(key + ".pub"), UTF_8));
        to.overrideLoginPrivateKey(Files.toString(new File(key), UTF_8));

        LOGGER.debug("Adding ssh inbound port");
        HashSet<Integer> ports = new HashSet<>();
        ports.add(22);
        int minPort = Integer.parseInt(hd.getImageProperties().get("adaptor-max-port"));
        int maxPort = Integer.parseInt(hd.getImageProperties().get("adaptor-min-port"));
        if (minPort > 0 && maxPort > 0) {
            for (int port = minPort; port < maxPort; ++port) {
                LOGGER.debug("Adding inbound port:" + port);
                ports.add(port);
            }
        }
        to.inboundPorts(Ints.toArray(ports));

        LOGGER.debug("Creating template with image " + hd.getImageName());
        return jcloudsClient.createTemplate(hd.getImageType(), hd.getImageName(), to);
    }

    private float getTotalDisk(List<? extends Volume> volumes) {
        float totalDisk = 0;
        for (Volume vol : volumes) {
            if (vol != null) {
                Float size = vol.getSize();
                if (size != null) {
                    totalDisk = totalDisk + size;
                }
            }
        }
        return totalDisk;
    }

    private String getIp(NodeMetadata vmd) throws ConnException {
        if (vmd.getPublicAddresses().isEmpty()) {
            if (vmd.getPrivateAddresses().isEmpty()) {
                throw new ConnException("No addresses found in the node description");
            } else {
                if (vmd.getPrivateAddresses().size() < ipIndex + 1) {
                    return vmd.getPrivateAddresses().iterator().next();
                } else {
                    return (String) vmd.getPrivateAddresses().toArray()[ipIndex];
                }
            }
        } else {
            if (vmd.getPublicAddresses().size() < ipIndex + 1) {
                return vmd.getPublicAddresses().iterator().next();
            } else {
                return (String) vmd.getPublicAddresses().toArray()[ipIndex];
            }
        }
    }

}
