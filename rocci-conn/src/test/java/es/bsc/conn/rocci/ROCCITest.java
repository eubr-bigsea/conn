package es.bsc.conn.rocci;

import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


public class ROCCITest {
    
    private static final Logger LOGGER = LogManager.getLogger("Console");
    
    @Test
    public void testEmpty() {
        // Nothing to check since ROCCI Server can be unavailable
    }

    //@Test
    public void testROCCI() throws ConnException {
        LOGGER.info("Hello Testing API code for rocci!"); // Display the string.

        /* sudo occi --endpoint https://rocci-server.bsc.es:11443
         *           --auth x509
         *           --ca-path /etc/grid-security/certificates/
         *           --user-cred ~/certs/test/scorella_test.pem
         *           --password ~/certs/test/scorella_test.key
         *           --action create
         *           --resource compute
         *           --attribute occi.core.title="VmwithUserCI"
         *           -M os_tpl#uuid_pmestestingocci_68
         *           -M resource_tpl#small
         *           --context user_data="file://$PWD/tmpfedcloud.login"
        */

        HardwareDescription hd = new HardwareDescription();
        SoftwareDescription sd = new SoftwareDescription();
        hd.setImageName("uuid_pmestestingocci_68");
        hd.setImageType("small");

        HashMap<String, String> prop = new HashMap<>();
        prop.put("Server", "https://rocci-server.bsc.es:11443");
        prop.put("auth", "x509");
        prop.put("password", "~/certs/test/scorella_test.key");
        prop.put("ca-path", "/etc/grid-security/certificates/");
        prop.put("user-cred", "~/certs/test/scorella_test.pem");
        prop.put("owner", "scorella");
        prop.put("jobname", "test");
        prop.put("context", "user_data=\"file://$PWD/tmpfedcloud.login\"");

        ROCCI r = new ROCCI(prop);

        // VirtualResource vr = (VirtualResource) r.create(hd, sd, prop);
        // LOGGER.info("VM id: "+vr.getId());
        // vr = r.waitUntilCreation(vr);
        // LOGGER.info("VM ip: "+vr.getIp());
        // r.destroy(vr.getId());

        String id = (String) r.create(hd, sd, prop);
        LOGGER.info("VM id: " + id);
        VirtualResource vr = r.waitUntilCreation(id);
        LOGGER.info("VM ip: " + vr.getIp());
        r.destroy(vr.getId());

        r.close();
    }
    
}