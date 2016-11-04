package es.bsc.conn;

import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.Map;


public abstract class Connector {

    // Properties' names
    protected static final String PROP_APP_NAME = "app-name";
    protected static final String PROP_SERVER = "Server";
    protected static final String PROP_TIME_SLOT = "time-slot";
    protected static final String PROP_ADAPTOR_MAX_PORT = "adaptor-max-port";
    protected static final String PROP_ADAPTOR_MIN_PORT = "adaptor-min-port";
    protected static final String PROP_KEYPAIR_NAME = "vm-keypair-name";
    protected static final String PROP_KEYPAIR_LOC = "vm-keypair-location";
    protected static final String PROP_MAX_VM_CREATION_TIME = "max-vm-creation-time";
    protected static final String PROP_MAX_CONNECTION_ERRORS = "max-connection-errors";

    // Properties' default values
    protected static final String DEFAULT_APP_NAME = "default-app";
    protected static final String DEFAULT_SERVER = null;
    protected static final long DEFAULT_TIME_SLOT = 5; // Minutes
    protected static final int DEFAULT_MAX_PORT = -1;
    protected static final int DEFAULT_MIN_PORT = -1;
    protected static final String DEFAULT_KEYPAIR_NAME = "";
    protected static final String DEFAULT_KEYPAIR_LOC = "";
    protected static final long DEFAULT_VM_CREATION_TIME = 10; // Minutes
    protected static final int DEFAULT_VM_CONNECTION_ERRORS = 3; // Number of maximum errors
    
    // Constants
    protected static final int SENCONDS_TO_MINUTES = 60;

    // Properties
    protected final String appName;
    protected final String server;
    protected final long timeSlot;
    protected final String keypairName;
    protected final String keypairLoc;
    protected final long maxVMCreationTime;
    protected final int maxVMConnectionErrors;


    /**
     * Initializes the common connector properties
     * 
     * @param props
     * @throws ConnException
     */
    public Connector(Map<String, String> props) throws ConnException {
        // Parse generic values from properties
        String propAppName = props.get(PROP_APP_NAME);
        if (propAppName != null && !propAppName.isEmpty()) {
            appName = propAppName;
        } else {
            appName = DEFAULT_APP_NAME;
        }

        String propServer = props.get(PROP_SERVER);
        if (propServer != null && !propServer.isEmpty()) {
            server = propServer;
        } else {
            server = DEFAULT_SERVER;
        }

        String propTimeSlot = props.get(PROP_TIME_SLOT);
        if (propTimeSlot != null && !propTimeSlot.isEmpty()) {
            // Move from Seconds to Minutes
            timeSlot = Long.parseLong(propTimeSlot) / SENCONDS_TO_MINUTES;
        } else {
            timeSlot = DEFAULT_TIME_SLOT;
        }

        String propKeypairName = props.get(PROP_KEYPAIR_NAME);
        if (propKeypairName != null && !propKeypairName.isEmpty()) {
            keypairName = propKeypairName;
        } else {
            keypairName = DEFAULT_KEYPAIR_NAME;
        }

        String propKeypairLoc = props.get(PROP_KEYPAIR_LOC);
        if (propKeypairLoc != null && !propKeypairLoc.isEmpty()) {
            keypairLoc = propKeypairLoc;
        } else {
            keypairLoc = DEFAULT_KEYPAIR_LOC;
        }

        String propMaxVMCreationTime = props.get(PROP_MAX_VM_CREATION_TIME);
        if (propMaxVMCreationTime != null && !propMaxVMCreationTime.isEmpty()) {
            // Move from seconds to minutes
            maxVMCreationTime = Long.parseLong(propMaxVMCreationTime) / SENCONDS_TO_MINUTES;
        } else {
            maxVMCreationTime = DEFAULT_VM_CREATION_TIME;
        }

        String propMaxVMConnectionErrors = props.get(PROP_MAX_CONNECTION_ERRORS);
        if (propMaxVMConnectionErrors != null && !propMaxVMConnectionErrors.isEmpty()) {
            maxVMConnectionErrors = Integer.parseInt(propMaxVMConnectionErrors);
        } else {
            maxVMConnectionErrors = DEFAULT_VM_CONNECTION_ERRORS;
        }
    }

    /**
     * Creates a VM with the given description and properties
     * 
     * @param hd
     * @param sd
     * @param prop
     * @return the vmId
     * 
     * @throws ConnException
     */
    public abstract Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException;

    /**
     * Waits until the vm with id @id is created
     * 
     * @param id
     * @return the VM description
     * @throws ConnException
     */
    public abstract VirtualResource waitUntilCreation(Object id) throws ConnException;

    /**
     * Destroys the given VM with @id
     * 
     * @param id
     */
    public abstract void destroy(Object id);

    /**
     * Returns the time slot
     * 
     * @return time slot
     */
    public long getTimeSlot() {
        return timeSlot;
    }

    /**
     * Returns the price per time slot
     * 
     * @param virtualResource
     * @return price per time slot
     */
    public abstract float getPriceSlot(VirtualResource virtualResource);

    /**
     * Closes the connector interface
     * 
     */
    public abstract void close();

}
