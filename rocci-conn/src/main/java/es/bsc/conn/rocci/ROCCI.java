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
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of ROCCI Connector version 4.2.5
 *
 */
public class ROCCI extends Connector {

    // ROCCI Client API version
    private static final String ROCCI_CLIENT_VERSION = "4.2.5";

    // Properties' names
    private static final String PROP_AUTH = "auth";
    private static final String PROP_TIMEOUT = "timeout";
    private static final String PROP_USERNAME = "username";
    private static final String PROP_PASSW = "password";
    private static final String PROP_CA_PATH = "ca-path";
    private static final String PROP_CA_FILE = "ca-file";
    private static final String PROP_SKIP_CA_CHECK = "skip-ca-check";
    private static final String PROP_FILTER = "filter";
    private static final String PROP_USER_CREDENTIALS = "user-cred";
    private static final String PROP_VOMS = "voms";
    private static final String PROP_MEDIA_TYPE = "media-type";
    private static final String PROP_RESOURCE = "resource";
    private static final String PROP_ATTRIBUTES = "attributes";
    private static final String PROP_CONTEXT = "context";
    private static final String PROP_ACTION = "action";
    private static final String PROP_MIXIN = "mixin";
    private static final String PROP_LINK = "link";
    private static final String PROP_TRIGGER_ACTION = "trigger-action";
    private static final String PROP_LOG = "log-to";
    private static final String PROP_DUMP_MODEL = "dump-model";
    private static final String PROP_DEBUG = "debug";
    private static final String PROP_VERBOSE = "verbose";
    private static final String PROP_ATTR_OWNER = "owner";
    private static final String PROP_ATTR_JOBNAME = "jobname";

    // ROCCI Properties' names
    private static final String ROCCI_PROP_SERVER = "--endpoint ";
    private static final String ROCCI_PROP_AUTH = "--auth ";
    private static final String ROCCI_PROP_TIMEOUT = "--timeout ";
    private static final String ROCCI_PROP_USERNAME = "--username ";
    private static final String ROCCI_PROP_PASSW = "--password ";
    private static final String ROCCI_PROP_CA_PATH = "--ca-path ";
    private static final String ROCCI_PROP_CA_FILE = "--ca-file ";
    private static final String ROCCI_PROP_SKIP_CA_CHECK = "--skip-ca-check ";
    private static final String ROCCI_PROP_FILTER = "--filter ";
    private static final String ROCCI_PROP_USER_CREDENTIALS = "--user-cred ";
    private static final String ROCCI_PROP_VOMS = "--voms";
    private static final String ROCCI_PROP_MEDIA_TYPE = "--media-type ";
    private static final String ROCCI_PROP_RESOURCE = "--resource ";
    private static final String ROCCI_PROP_ATTRIBUTES = "--attributes ";
    private static final String ROCCI_PROP_CONTEXT = "--context ";
    private static final String ROCCI_PROP_ACTION = "--action ";
    private static final String ROCCI_PROP_MIXIN = "--mixin ";
    private static final String ROCCI_PROP_LINK = "--link ";
    private static final String ROCCI_PROP_TRIGGER_ACTION = "--trigger-action ";
    private static final String ROCCI_PROP_LOG = "--log-to ";
    private static final String ROCCI_PROP_DUMP_MODEL = "--dump-model";
    private static final String ROCCI_PROP_DEBUG = "--debug";
    private static final String ROCCI_PROP_VERBOSE = "--verbose";
    private static final String ROCCI_PROP_OUTPUT_FORMAT = "--output-format json_extended_pretty";

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.ROCCI);

    // Retry time between petitions
    private static final Integer RETRY_TIME = 5; // Seconds

    // Client
    private final RocciClient client;

    // Information about requests
    private final Map<String, HardwareDescription> vmidToHardwareRequest = new HashMap<>();
    private final Map<String, SoftwareDescription> vmidToSoftwareRequest = new HashMap<>();


    /**
     * Initializes the ROCCI connector with the given properties
     * 
     * @param props
     */
    public ROCCI(Map<String, String> props) throws ConnException {
        super(props);

        // Log creation
        LOGGER.info("Starting ROCCI v" + ROCCI_CLIENT_VERSION);

        // ROCCI client parameters setup
        final ArrayList<String> cmdString = new ArrayList<>();
        if (server != null) {
            cmdString.add(ROCCI_PROP_SERVER + server);
        }

        String propAuth = props.get(PROP_AUTH);
        if (propAuth != null) {
            cmdString.add(ROCCI_PROP_AUTH + propAuth);
        }

        String propTimeout = props.get(PROP_TIMEOUT);
        if (propTimeout != null) {
            cmdString.add(ROCCI_PROP_TIMEOUT + propTimeout);
        }

        String propUsername = props.get(PROP_USERNAME);
        if (propUsername != null) {
            cmdString.add(ROCCI_PROP_USERNAME + propUsername);
        }

        String propPassword = props.get(PROP_PASSW);
        if (propPassword != null) {
            cmdString.add(ROCCI_PROP_PASSW + propPassword);
        }

        String propCAPath = props.get(PROP_CA_PATH);
        if (propCAPath != null) {
            cmdString.add(ROCCI_PROP_CA_PATH + propCAPath);
        }

        String propCAFile = props.get(PROP_CA_FILE);
        if (propCAFile != null) {
            cmdString.add(ROCCI_PROP_CA_FILE + propCAFile);
        }

        String propSkipCACheck = props.get(PROP_SKIP_CA_CHECK);
        if (propSkipCACheck != null) {
            cmdString.add(ROCCI_PROP_SKIP_CA_CHECK + propSkipCACheck);
        }

        String propFilter = props.get(PROP_FILTER);
        if (propFilter != null) {
            cmdString.add(ROCCI_PROP_FILTER + propFilter);
        }

        String propUserCred = props.get(PROP_USER_CREDENTIALS);
        if (propUserCred != null) {
            cmdString.add(ROCCI_PROP_USER_CREDENTIALS + propUserCred);
        }

        String propVoms = props.get(PROP_VOMS);
        if (propVoms != null) {
            cmdString.add(ROCCI_PROP_VOMS + propVoms);
        }

        String propMediaType = props.get(PROP_MEDIA_TYPE);
        if (propMediaType != null) {
            cmdString.add(ROCCI_PROP_MEDIA_TYPE + propMediaType);
        }

        String propResource = props.get(PROP_RESOURCE);
        if (propResource != null) {
            cmdString.add(ROCCI_PROP_RESOURCE + propResource);
        }

        String propAttributes = props.get(PROP_ATTRIBUTES);
        if (propAttributes != null) {
            cmdString.add(ROCCI_PROP_ATTRIBUTES + propAttributes);
        }

        String propContext = props.get(PROP_CONTEXT);
        if (propContext != null) {
            cmdString.add(ROCCI_PROP_CONTEXT + propContext);
        }

        String propAction = props.get(PROP_ACTION);
        if (propAction != null) {
            cmdString.add(ROCCI_PROP_ACTION + propAction);
        }

        String propMixin = props.get(PROP_MIXIN);
        if (propMixin != null) {
            cmdString.add(ROCCI_PROP_MIXIN + propMixin);
        }

        String propLink = props.get(PROP_LINK);
        if (propLink != null) {
            cmdString.add(ROCCI_PROP_LINK + propLink);
        }

        String propTriggerAction = props.get(PROP_TRIGGER_ACTION);
        if (propTriggerAction != null) {
            cmdString.add(ROCCI_PROP_TRIGGER_ACTION + propTriggerAction);
        }

        String propLog = props.get(PROP_LOG);
        if (propLog != null) {
            cmdString.add(ROCCI_PROP_LOG + propLog);
        }

        String propDumpModel = props.get(PROP_DUMP_MODEL);
        if (propDumpModel != null) {
            cmdString.add(ROCCI_PROP_DUMP_MODEL);
        }

        String propDebug = props.get(PROP_DEBUG);
        if (propDebug != null) {
            cmdString.add(ROCCI_PROP_DEBUG);
        }

        String propVerbose = props.get(PROP_VERBOSE);
        if (propVerbose != null) {
            cmdString.add(ROCCI_PROP_VERBOSE);
        }

        // Add output format
        cmdString.add(ROCCI_PROP_OUTPUT_FORMAT);

        // ROCCI connector attributes setup
        String owner = props.get(PROP_ATTR_OWNER);
        String jobName = props.get(PROP_ATTR_JOBNAME);
        String attributes = "";
        if (owner != null && jobName != null) {
            attributes = owner + "-" + jobName;
        }

        // Instantiate ROCCI client
        client = new RocciClient(cmdString, attributes);
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        try {
            String instanceCode = hd.getImageType();
            String vmId = client.createCompute(hd.getImageName(), instanceCode);

            vmidToHardwareRequest.put(vmId, hd);
            vmidToSoftwareRequest.put(vmId, sd);

            VirtualResource vr = new VirtualResource(vmId, hd, sd, prop);
            return vr.getId();
        } catch (Exception e) {
            LOGGER.error("Error creating a VM", e);
            throw new ConnException("Error creating a VM", e);
        }
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        LOGGER.debug("Waiting for creation " + id);

        String vmId = (String) id;
        LOGGER.info("Waiting until VM " + vmId + " is created");

        Integer polls = 0;
        int errors = 0;
        String status = null;
        do {
            try {
                Thread.sleep(RETRY_TIME * (long) 1_000);
                if (RETRY_TIME * polls >= maxVMCreationTime * 60) {
                    throw new ConnException("Maximum VM creation time reached.");
                }
                polls++;
                status = client.getResourceStatus(vmId);
            } catch (ConnClientException | ConnException | InterruptedException e) {
                errors++;
                if (errors == maxVMConnectionErrors) {
                    LOGGER.error("ERROR_MSG = [\n\tError = " + e.getMessage() + "\n]");
                    throw new ConnException("Error getting the status of the request", e);
                }
            }
        } while (status == null || !"active".equals(status));

        // Retrieve IP
        String ip = null;
        try {
            ip = client.getResourceAddress(vmId);
        } catch (ConnClientException cce) {
            throw new ConnException("Error retrieving resource address from client", cce);
        }

        // Create Virtual Resource
        VirtualResource vr = new VirtualResource();
        vr.setId(vmId);
        vr.setIp(ip);
        vr.setProperties(null);

        HardwareDescription hd = vmidToHardwareRequest.get(vmId);
        if (hd == null) {
            throw new ConnException("Unregistered hardware description for vmId = " + vmId);
        }
        try {
            getHardwareInformation(vmId, hd);
        } catch (ConnClientException cce) {
            throw new ConnException("Error retrieving resource hardware description of VM " + vmId + " from client", cce);
        }
        vr.setHd(hd);

        SoftwareDescription sd = vmidToSoftwareRequest.get(vmId);
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
        LOGGER.info(" Destroy VM " + vmId + " with rOCCI connector");

        client.deleteCompute(vmId);
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

    private void getHardwareInformation(String vmId, HardwareDescription hd) throws ConnClientException {
        Object[] grantedHD = client.getHardwareDescription(vmId);
        // grantedHD is of the form [memSize, storageSize, cores, architecture, speed]
        Float memory = (Float) grantedHD[0];
        Float storage = (Float) grantedHD[1];
        Integer cores = (Integer) grantedHD[2];
        String architecture = (String) grantedHD[3];
        Float speed = (Float) grantedHD[4];

        // Check cores and set default value is none
        if (cores == null || cores < 0) {
            cores = 1;
        }

        // Create a runtime processor
        Processor runtimeProc = new es.bsc.conn.types.Processor();
        runtimeProc.setComputingUnits(cores);
        if (architecture != null && !architecture.isEmpty()) {
            runtimeProc.setArchitecture(architecture);
        }
        if (speed != null) {
            runtimeProc.setSpeed(speed);
        }
        List<Processor> procs = new ArrayList<>();
        procs.add(runtimeProc);

        // Add Hardware information
        if (memory != null) {
            hd.setMemorySize(memory);
        }
        if (storage != null) {
            hd.setStorageSize(storage);
        }
        hd.setProcessors(procs);
        hd.setTotalComputingUnits(cores);
    }

}
