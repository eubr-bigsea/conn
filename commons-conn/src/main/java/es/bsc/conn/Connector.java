package es.bsc.conn;

import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;


public abstract class Connector {

    public Connector(HashMap<String, String> prop) {
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
    public abstract Object create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) throws ConnException;

    public abstract VirtualResource waitUntilCreation(Object id) throws ConnException;

    public abstract void destroy(Object id);

    public abstract long getTimeSlot();

    public abstract float getPriceSlot(VirtualResource virtualResource);

    public abstract void close();

}
