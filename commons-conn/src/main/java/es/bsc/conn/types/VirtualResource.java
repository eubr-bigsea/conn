package es.bsc.conn.types;

import java.util.Map;


public class VirtualResource {

    private static final String DEFAULT_ID = "-1";
    private static final String INVALID_IP = "-1.-1.-1.-1";

    private String ip;
    private Object id;
    private HardwareDescription hd;
    private SoftwareDescription sd;
    private Map<String, String> properties;


    /**
     * Creates a new virtual resource with the default values
     */
    public VirtualResource() {
        this.id = DEFAULT_ID;
        this.ip = INVALID_IP;
        this.hd = null;
        this.sd = null;
        this.properties = null;
    }

    /**
     * Creates a new virtual resource from the given values
     * 
     * @param id
     * @param hd
     * @param sd
     * @param prop
     */
    public VirtualResource(Object id, HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) {
        this.id = id;
        this.ip = INVALID_IP;
        this.hd = hd;
        this.sd = sd;
        this.properties = prop;
    }

    /**
     * Returns the IP
     * 
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets a new IP
     * 
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Returns the ID
     * 
     * @return
     */
    public Object getId() {
        return id;
    }

    /**
     * Sets a new ID
     * 
     * @param id
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * Returns the hardware description
     * 
     * @return
     */
    public HardwareDescription getHd() {
        return hd;
    }

    /**
     * Sets the hardware description
     * 
     * @param hd
     */
    public void setHd(HardwareDescription hd) {
        this.hd = hd;
    }

    /**
     * Returns the software description
     * 
     * @return
     */
    public SoftwareDescription getSd() {
        return sd;
    }

    /**
     * Sets the software description
     * 
     * @param sd
     */
    public void setSd(SoftwareDescription sd) {
        this.sd = sd;
    }

    /**
     * Returns the virtual resource properties
     * 
     * @return
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets the virtual resource properties
     * 
     * @param properties
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
