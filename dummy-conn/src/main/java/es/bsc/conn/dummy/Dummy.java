package es.bsc.conn.dummy;

import es.bsc.conn.Connector;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Dummy extends Connector {

    private static final Logger LOGGER = LogManager.getLogger(Loggers.DUMMY);


    public Dummy(HashMap<String, String> prop) {
        super(prop);
    }

    @Override
    public VirtualResource create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) throws ConnException {
        LOGGER.info("creating VirtualResource");
        return new VirtualResource();
    }
    
    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        VirtualResource vr = (VirtualResource) id;
        LOGGER.info("waiting VirtualResource");
        return vr;
    }

    @Override
    public void destroy(Object id) {
        LOGGER.info("deleting VirtualResource");
    }

    @Override
    public long getTimeSlot() {
        LOGGER.info("getting time slot");
        return 1;
    }

    @Override
    public float getPriceSlot(VirtualResource virtualResource) {
        LOGGER.info("getting price slot");
        return (float) 1.0;
    }

    @Override
    public void close() {
        LOGGER.info("closing");
    }

}
