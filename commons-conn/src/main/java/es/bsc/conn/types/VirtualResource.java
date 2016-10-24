package es.bsc.conn.types;

import java.util.HashMap;


public class VirtualResource {

    private String ip;
    private Object id;
    private HardwareDescription hd;
    private SoftwareDescription sd;
    private HashMap<String, String> properties;


    public VirtualResource() {
        this.id = "-1";
        this.ip = "0.0.0.0";
        this.hd = null;
        this.sd = null;
        this.properties = null;
    }

    public VirtualResource(Object id, HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) {
        this.id = id;
        this.ip = "-1.-1.-1.-1";
        this.hd = hd;
        this.sd = sd;
        this.properties = prop;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public HardwareDescription getHd() {
        return hd;
    }

    public void setHd(HardwareDescription hd) {
        this.hd = hd;
    }

    public SoftwareDescription getSd() {
        return sd;
    }

    public void setSd(SoftwareDescription sd) {
        this.sd = sd;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }
    
}
