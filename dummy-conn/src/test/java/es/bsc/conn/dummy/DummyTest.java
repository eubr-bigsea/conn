package es.bsc.conn.dummy;

import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


public class DummyTest {

    private static final Logger LOGGER = LogManager.getLogger("Console");


    @Test
    public void apiTest() {
        LOGGER.info("Hello Testing API code!"); // Display the string.

        Dummy d = new Dummy(new HashMap<String, String>());
        HardwareDescription hd = new HardwareDescription();
        SoftwareDescription sd = new SoftwareDescription();
        HashMap<String, String> prop = new HashMap<String, String>();
        Object id = d.create(hd, sd, prop);

        // Integer id = (Integer) d.create(hd, sd, prop);
        LOGGER.info("VM id: " + id);
        d.close();
    }
}
