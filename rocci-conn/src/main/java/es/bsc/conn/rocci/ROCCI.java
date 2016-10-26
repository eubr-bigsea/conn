package es.bsc.conn.rocci;

import es.bsc.conn.Connector;
import es.bsc.conn.clients.exceptions.ConnClientException;
import es.bsc.conn.clients.rocci.RocciClient;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.Processor;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ROCCI extends Connector {
    
    private static final Logger logger = LogManager.getLogger(Loggers.ROCCI);

    private static final String ROCCI_CLIENT_VERSION = "4.2.5";

    private static final Integer RETRY_TIME = 5; // Seconds
    private static final long DEFAULT_TIME_SLOT = 5; // Minutes
    
    private static final HashMap<String, HardwareDescription> VMID_TO_HARDWARE_REQUEST = new HashMap<String, HardwareDescription>();
    private static final HashMap<String, SoftwareDescription> VMID_TO_SOFTWARE_REQUEST = new HashMap<String, SoftwareDescription>();

    private final RocciClient client;
    private static Integer MAX_VM_CREATION_TIME = 10; // Minutes
    private static Integer MAX_ALLOWED_ERRORS = 3; // Number of maximum errors
    private long timeSlot = DEFAULT_TIME_SLOT;


    public ROCCI(HashMap<String, String> props) {
        super(props);
        
        // Log creation
        logger.info("Starting ROCCI v" + ROCCI_CLIENT_VERSION);

        // ROCCI client parameters setup
        final ArrayList<String> cmd_string = new ArrayList<String>();
        if (props.get("Server") != null) {
            cmd_string.add("--endpoint " + props.get("Server"));
        }

        if (props.get("auth") != null) {
            cmd_string.add("--auth " + props.get("auth"));
        }

        if (props.get("timeout") != null) {
            cmd_string.add("--timeout " + props.get("timeout"));
        }

        if (props.get("username") != null) {
            cmd_string.add("--username " + props.get("username"));
        }

        if (props.get("password") != null) {
            cmd_string.add("--password " + props.get("password"));
        }

        if (props.get("ca-path") != null) {
            cmd_string.add("--ca-path " + props.get("ca-path"));
        }

        if (props.get("ca-file") != null) {
            cmd_string.add("--ca-file " + props.get("ca-file"));
        }

        if (props.get("skip-ca-check") != null) {
            cmd_string.add("--skip-ca-check " + props.get("skip-ca-check"));
        }

        if (props.get("filter") != null) {
            cmd_string.add("--filter " + props.get("filter"));
        }

        if (props.get("user-cred") != null) {
            cmd_string.add("--user-cred " + props.get("user-cred"));
        }

        if (props.get("voms") != null) {
            cmd_string.add("--voms");
        }

        if (props.get("media-type") != null) {
            cmd_string.add("--media-type " + props.get("media-type"));
        }

        if (props.get("resource") != null)
            cmd_string.add("--resource " + props.get("resource"));

        if (props.get("attributes") != null)
            cmd_string.add("--attributes " + props.get("attributes"));

        if (props.get("context") != null) {
            cmd_string.add("--context " + props.get("context"));
        }

        if (props.get("action") != null)
            cmd_string.add("--action " + props.get("action"));

        if (props.get("mixin") != null)
            cmd_string.add("--mixin " + props.get("mixin"));

        if (props.get("link") != null) {
            cmd_string.add("--link " + props.get("link"));
        }

        if (props.get("trigger-action") != null) {
            cmd_string.add("--trigger-action " + props.get("trigger-action"));
        }

        if (props.get("log-to") != null) {
            cmd_string.add("--log-to " + props.get("log-to"));
        }

        cmd_string.add("--output-format json_extended_pretty");

        if (props.get("dump-model") != null) {
            cmd_string.add("--dump-model");
        }

        if (props.get("debug") != null) {
            cmd_string.add("--debug");
        }

        if (props.get("verbose") != null) {
            cmd_string.add("--verbose");
        }

        // ROCCI connector parameters setup
        if (props.get("max-vm-creation-time") != null) {
            MAX_VM_CREATION_TIME = Integer.parseInt(props.get("max-vm-creation-time"));
        }

        if (props.get("max-connection-errors") != null) {
            MAX_ALLOWED_ERRORS = Integer.parseInt(props.get("max-connection-errors"));
        }

        // ROCCI connector attributes setup
        String attributes = new String("");
        if (props.get("owner") != null && props.get("jobname") != null) {
            attributes = props.get("owner") + "-" + props.get("jobname");
        }

        String time = props.get("time-slot");
        if (time != null) {
            // Move from MS to S
            timeSlot = Long.parseLong(time) * 1_000;
        } else {
            timeSlot = DEFAULT_TIME_SLOT;
        }

        client = new RocciClient(cmd_string, attributes);
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) throws ConnException {
        try {
            String instanceCode = hd.getImageType();
            String vmId = client.create_compute(hd.getImageName(), instanceCode);   
            
            VMID_TO_HARDWARE_REQUEST.put(vmId,  hd);
            VMID_TO_SOFTWARE_REQUEST.put(vmId,  sd);
            
            VirtualResource vr = new VirtualResource(vmId, hd, sd, prop);
            return vr.getId();
        } catch (Exception e) {
            logger.error("Error creating a VM", e);
            throw new ConnException("Error creating a VM", e);
        }
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        logger.debug("Waiting for creation " + id);
        
        String vmId = (String) id;
        logger.info("Waiting until VM " + vmId + " is created");
        
        Integer polls = 0;
        int errors = 0;
        String status = null;
        do {
            try {
                Thread.sleep(RETRY_TIME * 1_000);
                if (RETRY_TIME * polls >= MAX_VM_CREATION_TIME * 60) {
                    throw new ConnException("Maximum VM creation time reached.");
                }
                polls++;
                status = client.get_resource_status(vmId);
            } catch (Exception e) {
                errors++;
                if (errors == MAX_ALLOWED_ERRORS) {
                    // logger.error("ERROR_MSG = [\n\tError = " + e.getMessage() + "\n]");
                    logger.error("ERROR_MSG = [\n\tError = " + e.getMessage() + "\n]");
                    throw new ConnException("Error getting the status of the request");
                }
            }
        } while (status == null || !status.equals("active"));
        
        // Retrieve IP
        String ip = null;
        try {
            ip = client.get_resource_address(vmId);
        } catch (ConnClientException cce) {
            throw new ConnException("Error retrieving resource address from client", cce);
        }

        // Create Virtual Resource
        VirtualResource vr = new VirtualResource();
        vr.setId(vmId);
        vr.setIp(ip);
        vr.setProperties(null);

        HardwareDescription hd = VMID_TO_HARDWARE_REQUEST.get(vmId);
        if (hd == null) {
            throw new ConnException("Unregistered hardware description for vmId = " + vmId);
        }
        try {
            getHardwareInformation(vmId, hd);
        } catch (ConnClientException cce) {
            throw new ConnException("Error retrieving resource hardware description of VM " + vmId + " from client", cce);
        }
        vr.setHd(hd);

        SoftwareDescription sd = VMID_TO_SOFTWARE_REQUEST.get(vmId);
        if (sd == null) {
            throw new ConnException("Unregistered software description for vmId = " + vmId);
        }
        sd.setOperatingSystemType("Linux");
        vr.setSd(sd);

        return vr;
    }

    @Override
    public void destroy(Object id) {
        String vmId = (String) id;
        logger.info(" Destroy VM " + vmId + " with rOCCI connector");
        
        client.delete_compute(vmId);
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
    
    private void getHardwareInformation(String vmId, HardwareDescription hd) throws ConnClientException {
        Object[] grantedHD = client.get_hardware_description(vmId);
        // grantedHD is of the form {memSize, storageSize, cores, architecture, speed}
        Float memory = (Float) grantedHD[0];
        Float storage = (Float) grantedHD[1];
        Integer cores = (Integer) grantedHD[2];
        String architecture = (String) grantedHD[3];
        Float speed = (Float) grantedHD[4];
        
        // Create a runtime processor
        Processor runtime_proc = new es.bsc.conn.types.Processor();
        runtime_proc.setComputingUnits(cores);
        runtime_proc.setArchitecture(architecture);
        runtime_proc.setSpeed(speed);
        List<Processor> procs = new ArrayList<Processor>();
        procs.add(runtime_proc);
    
        // Add Hardware information
        hd.setMemorySize(memory);
        hd.setStorageSize(storage);
        hd.setProcessors(procs);
        hd.setTotalComputingUnits(cores);
    }

}
