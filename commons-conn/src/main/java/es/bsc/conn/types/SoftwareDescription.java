package es.bsc.conn.types;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of a Virtual Resource Software Description
 *
 */
public class SoftwareDescription {

    // Operating System
    protected String operatingSystemType = BasicTypes.UNASSIGNED_STR;
    protected String operatingSystemDistribution = BasicTypes.UNASSIGNED_STR;
    protected String operatingSystemVersion = BasicTypes.UNASSIGNED_STR;

    // Applications
    protected List<String> appSoftware = new LinkedList<>();


    /**
     * Creates a new software description with empty values
     */
    public SoftwareDescription() {
        this.operatingSystemType = "";
        this.operatingSystemDistribution = "";
        this.operatingSystemVersion = "";
    }

    /**
     * Creates a new software description from the given values
     * 
     * @param operatingSystemType
     * @param operatingSystemDistribution
     * @param operatingSystemVersion
     * @param appSoftware
     */
    public SoftwareDescription(String operatingSystemType, String operatingSystemDistribution, String operatingSystemVersion,
            List<String> appSoftware) {

        this.operatingSystemType = operatingSystemType;
        this.operatingSystemDistribution = operatingSystemDistribution;
        this.operatingSystemVersion = operatingSystemVersion;
        this.appSoftware = appSoftware;
    }

    /**
     * Returns the operating system type
     * 
     * @return
     */
    public String getOperatingSystemType() {
        return operatingSystemType;
    }

    /**
     * Sets the operating system type
     * 
     * @param osType
     */
    public void setOperatingSystemType(String osType) {
        this.operatingSystemType = osType;
    }

    /**
     * Returns the operating system distribution
     * 
     * @return
     */
    public String getOperatingSystemDistribution() {
        return operatingSystemDistribution;
    }

    /**
     * Sets the operating system distribution
     * 
     * @param osDistribution
     */
    public void setOperatingSystemDistribution(String osDistribution) {
        this.operatingSystemDistribution = osDistribution;
    }

    /**
     * Returns the operating system version
     * 
     * @return
     */
    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    /**
     * Sets the operating system version
     * 
     * @param osVersion
     */
    public void setOperatingSystemVersion(String osVersion) {
        this.operatingSystemVersion = osVersion;
    }

    /**
     * Returns the required application software
     * 
     * @return
     */
    public List<String> getAppSoftware() {
        return appSoftware;
    }

    /**
     * Sets the required applications software
     * 
     * @param appSoftware
     */
    public void setAppSoftware(List<String> appSoftware) {
        this.appSoftware = appSoftware;
    }

}
