package es.bsc.conn.vmm;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.bsc.conn.Connector;
import es.bsc.conn.clients.exceptions.ConnClientException;
import es.bsc.conn.clients.vmm.VMMClient;
import es.bsc.conn.clients.vmm.types.VMDescription;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;


public class VMMConnector extends Connector {

    // Constants
    private static final String ACTIVE = "ACTIVE";
    private static final String ERROR = "ERROR";
    private static final long POLLING_INTERVAL = 5;
    private static final int TIMEOUT = 1_800;

    // Logger
    private static final Logger logger = LogManager.getLogger(Loggers.VMM);

    // VMM Client
    private VMMClient client;

    // Information about requests
    private final Map<String, HardwareDescription> vmidToHardwareRequest = new HashMap<>();
    private final Map<String, SoftwareDescription> vmidToSoftwareRequest = new HashMap<>();


    public VMMConnector(Map<String, String> props) throws ConnException {
        super(props);
        this.client = new VMMClient(server);
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        try {
            String vmId = client.createVM(hd.getImageName(), hd.getTotalComputingUnits(), (int) (hd.getMemorySize() * 1_000),
                    (int) hd.getStorageSize(), true);

            vmidToHardwareRequest.put(vmId, hd);
            vmidToSoftwareRequest.put(vmId, sd);

            VirtualResource vr = new VirtualResource(vmId, hd, sd, prop);
            return vr.getId();
        } catch (ConnClientException ce) {
            logger.error("Exception submitting vm creation", ce);
            throw new ConnException(ce);
        }
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        logger.debug("Waiting for creation " + id);
        String vmId = (String) id;
        logger.info("Waiting until VM " + vmId + " is created");

        try {
            VMDescription vmd = client.getVMDescription(vmId);
            logger.info("VM State is " + vmd.getState());
            int tries = 0;
            while (vmd.getState() == null || !vmd.getState().equals(ACTIVE)) {
                if (vmd.getState().equals(ERROR)) {
                    logger.error("Error waiting for VM Creation. Middleware has return an error state");
                    throw new ConnException("Error waiting for VM Creation. Middleware has return an error state");
                }
                if (tries * POLLING_INTERVAL > TIMEOUT) {
                    throw new ConnException("Maximum VM creation time reached.");
                }

                tries++;

                Thread.sleep(POLLING_INTERVAL * 1_000);

                vmd = client.getVMDescription(vmId);
            }

            // Create Virtual Resource
            VirtualResource vr = new VirtualResource();
            vr.setId(vmId);
            vr.setIp(vmd.getIpAddress());
            vr.setProperties(null);

            HardwareDescription hd = vmidToHardwareRequest.get(vmId);
            if (hd == null) {
                throw new ConnException("Unregistered hardware description for vmId = " + vmId);
            }
            hd.setTotalComputingUnits(vmd.getCpus());
            hd.setMemorySize(vmd.getRamMb());
            hd.setStorageSize(vmd.getDiskGb());
            hd.setImageName(vmd.getImage());
            vr.setHd(hd);

            SoftwareDescription sd = vmidToSoftwareRequest.get(vmId);
            if (sd == null) {
                throw new ConnException("Unregistered software description for vmId = " + vmId);
            }
            sd.setOperatingSystemType("Linux");
            vr.setSd(sd);

            return vr;
        } catch (ConnClientException | InterruptedException e) {
            logger.error("Exception waiting for VM Creation");
            throw new ConnException(e);
        }
    }

    @Override
    public void destroy(Object id) {
        String vmId = (String) id;
        try {
            client.deleteVM(vmId);
            vmidToHardwareRequest.remove(vmId);
            vmidToSoftwareRequest.remove(vmId);
        } catch (ConnClientException cce) {
            logger.error("Exception waiting for VM Destruction", cce);
        }
    }

    @Override
    public float getPriceSlot(VirtualResource virtualResource) {
        return virtualResource.getHd().getPricePerUnit();
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
