package es.bsc.conn.dummy;
import es.bsc.conn.Connector;
import es.bsc.conn.exceptions.ConnectorException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;

/**
 * Created by bscuser on 7/29/16.
 */
public class Dummy extends Connector {
    public Dummy(HashMap<String, String> prop){
        super(prop);
    }
    public VirtualResource create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop){
        System.out.println("creating VirtualResource");
        return new VirtualResource();
    }
    public void destroy(Object id){
        System.out.println("deleting VirtualResource");
    }
    public long getTimeSlot(){
        System.out.println("getting time slot");
        return 1;
    }
    public float getPriceSlot(VirtualResource virtualResource){
        System.out.println("getting price slot");
        return (float) 1.0;
    }
    public void close(){
        System.out.println("closing");
    }
	@Override
	public VirtualResource waitUntilCreation(Object id)
			throws ConnectorException {
		VirtualResource vr = (VirtualResource)id;
		System.out.println("waiting VirtualResource");
        return vr;
		
	}
}
