package es.bsc.conn.dummy;
import es.bsc.conn.Connector;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.Vm;

import java.util.HashMap;

/**
 * Created by bscuser on 7/29/16.
 */
public class Dummy extends Connector {
    public Dummy(HashMap<String, String> prop){
        super(prop);
    }
    public Object create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop){
        System.out.println("creating Vm");
        return 1;
    }
    public Vm waitUntilCreation(Object id){
        System.out.println("waiting Vm");
        return null;
    }
    public void destroy(Object id){
        System.out.println("deleting Vm");
    }
    public long getTimeSlot(){
        System.out.println("getting time slot");
        return 1;
    }
    public float getPriceSlot(){
        System.out.println("getting price slot");
        return (float) 1.0;
    }
    public void close(){
        System.out.println("closing");
    }
}
