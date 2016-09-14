package es.bsc.conn.rocci;
/*
Test app for rocci connector
 */

import es.bsc.conn.exceptions.ConnectorException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;

public class test {
    public static void main(String[] args) throws ConnectorException {
        System.out.println("Hello Testing API code for rocci!"); // Display the string.
        /* sudo occi --endpoint https://rocci-server.bsc.es:11443
                     --auth x509
                     --ca-path /etc/grid-security/certificates/
                     --user-cred ~/certs/test/scorella_test.pem
                     --password ~/certs/test/scorella_test.key
                     --action create
                     --resource compute
                     --attribute occi.core.title="VmwithUserCI"
                     -M os_tpl#uuid_pmestestingocci_68
                     -M resource_tpl#small
                     --context user_data="file://$PWD/tmpfedcloud.login"*/

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

        //VirtualResource vr = (VirtualResource) r.create(hd, sd, prop);
        //System.out.println("VM id: "+vr.getId());
        //vr = r.waitUntilCreation(vr);
        //System.out.println("VM ip: "+vr.getIp());
        //r.destroy(vr.getId());

        String id = (String) r.create(hd, sd, prop);
        System.out.println("VM id: "+id);
        VirtualResource vr = r.waitUntilCreation(id);
        System.out.println("VM ip: "+vr.getIp());
        r.destroy(vr.getId());

        r.close();

    }}