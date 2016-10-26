package es.bsc.conn;

import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.Map;


public abstract class Connector {

    public Connector(Map<String, String> prop) throws ConnException {
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
    public abstract long getTimeSlot();

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
