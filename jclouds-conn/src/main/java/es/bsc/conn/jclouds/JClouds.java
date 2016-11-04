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

    // Properties' names
    private static final String PROP_PROVIDER = "provider";
    private static final String PROP_PROVIDER_USER = "provider-user";
    private static final String PROP_PROVIDER_USER_CRED = "provider-user-credential";
    private static final String PROP_IP_INDEX = "ip-index";
    private static final int SSH_DEFAULT_PORT = 22;

    // Conversion units
    private static final int MS_TO_S = 1_000;
    private static final int MB_TO_GB = 1_024;

    // Time/ip properties
    private static final long POLLING_INTERVAL = 5;
    private static final int TIMEOUT = 1_800;
    private static final int DEFAULT_IP_INDEX = 0;

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.JCLOUDS);

    // Properties values
    private final String provider;
    private final String providerUser;
    private final String providerUserCred;
    private final int ipIndex;

    // Client
    private final JCloudsClient jcloudsClient;

    // Information about requests
    private final Map<String, HardwareDescription> vmidToHardwareRequest = new HashMap<>();
    private final Map<String, SoftwareDescription> vmidToSoftwareRequest = new HashMap<>();


    /**
     * Initializes the JClouds connector with the given properties
     * 
     * @param props
     */
    public JClouds(Map<String, String> props) throws ConnException {
        super(props);

        // JClouds client parameters setup
        provider = props.get(PROP_PROVIDER);
        if (provider == null) {
            throw new ConnException("Provider must be specified with \"provider\" property");
        }

        providerUser = props.get(PROP_PROVIDER_USER);
        if (providerUser == null) {
            throw new ConnException("Provider user must be specified with \"provider-user\" property");
        }

        providerUserCred = props.get(PROP_PROVIDER_USER_CRED);
        if (providerUserCred == null) {
            throw new ConnException("Provider user credential must be specified with \"provider-user-credential\" property");
        }

        String index = props.get(PROP_IP_INDEX);
        if (index != null) {
            ipIndex = Integer.parseInt(index);
        } else {
            ipIndex = DEFAULT_IP_INDEX;
        }

        try {
            jcloudsClient = new JCloudsClient(providerUser, providerUserCred, provider, server);
        } catch (ConnClientException cce) {
            throw new ConnException("Exception creating client", cce);
        }
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        try {
            Template template = generateTemplate(hd);
            Set<? extends NodeMetadata> vms = jcloudsClient.createVMS(appName, 1, template);

            String vmId = vms.iterator().next().getId();
            vmidToHardwareRequest.put(vmId, hd);
            vmidToSoftwareRequest.put(vmId, sd);

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
                Thread.sleep(POLLING_INTERVAL * MS_TO_S);
                vmNodeMetadata = jcloudsClient.getNode(vmId);
            }
            String ip = getIp(vmNodeMetadata);

            // Create Virtual Resource
            VirtualResource vr = new VirtualResource();
            vr.setId(vmId);
            vr.setIp(ip);
            vr.setProperties(null);

            HardwareDescription hd = vmidToHardwareRequest.get(vmId);
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

            SoftwareDescription sd = vmidToSoftwareRequest.get(vmId);
            if (sd == null) {
                throw new ConnException("Unregistered software description for vmId = " + vmId);
            }
            sd.setOperatingSystemType("Linux");
            vr.setSd(sd);

            return vr;
        } catch (ConnException | InterruptedException e) {
            LOGGER.error("Exception waiting for VM Creation");
            throw new ConnException("Exception waiting for VM Creation", e);
        }
    }

    @Override
    public void destroy(Object id) {
        String vmId = (String) id;

        jcloudsClient.destroyNode(vmId);
        vmidToHardwareRequest.remove(vmId);
        vmidToSoftwareRequest.remove(vmId);
    }

    @Override
    public float getPriceSlot(VirtualResource virtualResource) {
        return virtualResource.getHd().getPricePerUnit();
    }

    @Override
    public void close() {
        // Nothing to do
    }

    private Template generateTemplate(HardwareDescription hd) throws IOException {
        TemplateOptions to = new TemplateOptions();

        String key = keypairLoc + keypairName;
        LOGGER.debug("Authorizing keys :" + key);
        to.authorizePublicKey(Files.toString(new File(key + ".pub"), UTF_8));
        to.overrideLoginPrivateKey(Files.toString(new File(key), UTF_8));

        LOGGER.debug("Adding ssh inbound port");
        HashSet<Integer> ports = new HashSet<>();
        ports.add(SSH_DEFAULT_PORT);
        
        // Add Adaptor Ports
        int adaptorMaxPort = DEFAULT_MAX_PORT;
        String propAdaptorMaxPort = hd.getImageProperties().get(PROP_ADAPTOR_MAX_PORT);
        if (propAdaptorMaxPort != null && !propAdaptorMaxPort.isEmpty()) {
            adaptorMaxPort = Integer.parseInt(propAdaptorMaxPort);
        }
        int adaptorMinPort = DEFAULT_MIN_PORT;
        String propAdaptorMinPort = hd.getImageProperties().get(PROP_ADAPTOR_MIN_PORT);
        if (propAdaptorMinPort != null && !propAdaptorMinPort.isEmpty()) {
            adaptorMinPort = Integer.parseInt(propAdaptorMinPort);
        }
        
        if (adaptorMaxPort > 0 && adaptorMinPort > 0) {
            for (int port = adaptorMinPort; port < adaptorMaxPort; ++port) {
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
