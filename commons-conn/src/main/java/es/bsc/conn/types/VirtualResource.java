package es.bsc.conn.types;

import java.util.HashMap;


public class VirtualResource {
    
    private static final String DEFAULT_ID = "-1";
    private static final String DEFAULT_IP = "0.0.0.0";
    private static final String INVALID_IP = "-1.-1.-1.-1";

    private String ip;
    private Object id;
    private HardwareDescription hd;
    private SoftwareDescription sd;
    private HashMap<String, String> properties;


    public VirtualResource() {
        this.id = DEFAULT_ID;
        this.ip = DEFAULT_IP;
        this.hd = null;
        this.sd = null;
        this.properties = null;
    }

    public VirtualResource(Object id, HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) {
        this.id = id;
        this.ip = INVALID_IP;
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
