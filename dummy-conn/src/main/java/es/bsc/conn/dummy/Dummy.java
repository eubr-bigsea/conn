package es.bsc.conn.dummy;

import es.bsc.conn.Connector;
import es.bsc.conn.exceptions.ConnectorException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Dummy extends Connector {

    private static final Logger logger = LogManager.getLogger(Loggers.DUMMY);


    public Dummy(HashMap<String, String> prop) {
        super(prop);
    }

    public VirtualResource create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) {
        logger.info("creating VirtualResource");
        return new VirtualResource();
    }

    public void destroy(Object id) {
        logger.info("deleting VirtualResource");
    }

    public long getTimeSlot() {
        logger.info("getting time slot");
        return 1;
    }

    public float getPriceSlot(VirtualResource virtualResource) {
        logger.info("getting price slot");
        return (float) 1.0;
    }

    public void close() {
        logger.info("closing");
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnectorException {
        VirtualResource vr = (VirtualResource) id;
        logger.info("waiting VirtualResource");
        return vr;
    }

}
