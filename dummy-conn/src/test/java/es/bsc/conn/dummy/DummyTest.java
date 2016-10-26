package es.bsc.conn.dummy;

import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


public class DummyTest {

    private static final Logger LOGGER = LogManager.getLogger("Console");


    @Test
    public void apiTest() throws Exception {
        LOGGER.info("Hello Testing API code!"); // Display the string.

        Dummy d;
        try {
            d = new Dummy(new HashMap<String, String>());
        } catch (ConnException ce) {
            LOGGER.error("Exception creating Dummy connector", ce);
            throw ce;
        }
        HardwareDescription hd = new HardwareDescription();
        SoftwareDescription sd = new SoftwareDescription();
        HashMap<String, String> prop = new HashMap<String, String>();

        Object id = null;
        try {
            id = d.create(hd, sd, prop);
        } catch (ConnException ce) {
            LOGGER.error("Exception creating vm", ce);
            throw ce;
        }

        // Integer id = (Integer) d.create(hd, sd, prop);
        LOGGER.info("VM id: " + id);
        d.close();
    }
}
