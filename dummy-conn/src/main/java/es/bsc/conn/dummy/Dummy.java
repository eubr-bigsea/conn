package es.bsc.conn.dummy;

import es.bsc.conn.Connector;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Dummy extends Connector {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.DUMMY);

    // ID
    private static final int BASE_ID = 100;
    private static final String BASE_IP = "127.0.0.";
    private static AtomicInteger nextId = new AtomicInteger(BASE_ID);


    public Dummy(Map<String, String> props) throws ConnException {
        super(props);
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        LOGGER.info("Creating VirtualResource");
        LOGGER.debug("Hardware Description: " + hd);
        LOGGER.debug("Software Description: " + sd);

        TestEnvId envId = new TestEnvId();

        LOGGER.info("Assigned ID: " + envId);
        return envId;
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        try {
            Thread.sleep(15_000);
        } catch (InterruptedException ie) {
            throw new ConnException(ie);
        }

        LOGGER.info("Waiting VirtualResource " + id);
        VirtualResource vr = new VirtualResource();
        vr.setId(id);
        vr.setIp(BASE_IP + nextId.getAndIncrement());

        return vr;
    }

    @Override
    public void destroy(Object id) {
        LOGGER.info("Deleting VirtualResource " + id);
    }

    @Override
    public float getPriceSlot(VirtualResource virtualResource) {
        LOGGER.info("Getting price slot");
        return 0.0f;
    }

    @Override
    public void close() {
        LOGGER.info("Closing");
    }


    private static class TestEnvId {

        private static AtomicInteger nextId = new AtomicInteger(0);
        private int id = nextId.getAndIncrement();


        @Override
        public String toString() {
            return "TestEventId:" + id;
        }
    }

}
