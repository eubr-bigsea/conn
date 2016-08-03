package es.bsc.conn;

import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.Vm;

import java.util.HashMap;

public abstract class Connector {
    public Connector(HashMap<String, String> prop) {}
    public abstract Integer create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop);
    public abstract Vm waitUntilCreation(Integer id);
    public abstract void destroy(Integer id);
    public abstract long getTimeSlot();
    public abstract float getPriceSlot();
    public abstract void close();
}
