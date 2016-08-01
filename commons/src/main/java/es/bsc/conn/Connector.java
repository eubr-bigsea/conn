package es.bsc.conn;

import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.Vm;

import java.util.HashMap;

public interface Connector {
    public Integer create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop);
    public Vm waitUntilCreation(Integer id);
    public void destroy(Integer id);
    public long getTimeSlot();
    public float getPriceSlot();
    public void close();
}
