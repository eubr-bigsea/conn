package es.bsc.conn;

import es.bsc.conn.exceptions.ConnectorException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;

public abstract class Connector {
    public Connector(HashMap<String, String> prop) {}
    public abstract Object create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) throws ConnectorException;
    //public abstract VirtualResource waitUntilCreation(VirtualResource vr) throws ConnectorException;
    public abstract VirtualResource waitUntilCreation(Object id) throws ConnectorException;
    public abstract void destroy(Object id);
    public abstract long getTimeSlot();
    public abstract float getPriceSlot(VirtualResource virtualResource);
    public abstract void close();
}
