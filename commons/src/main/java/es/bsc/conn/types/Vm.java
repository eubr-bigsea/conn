package es.bsc.conn.types;

import java.util.HashMap;

public class Vm {
    private String ip;
    private Object id;
    private HardwareDescription hd;
    private SoftwareDescription sd;
    private HashMap<String, String> properties;

    public Vm(){

    }

    public Vm(String ip, Object id, HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> properties) {
        this.ip = ip;
        this.id = id;
        this.hd = hd;
        this.sd = sd;
        this.properties = properties;
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
