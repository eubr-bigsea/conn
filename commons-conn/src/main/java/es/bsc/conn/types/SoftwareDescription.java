package es.bsc.conn.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class SoftwareDescription {

    // Operating System
    protected String operatingSystemType = BasicTypes.UNASSIGNED_STR;
    protected String operatingSystemDistribution = BasicTypes.UNASSIGNED_STR;
    protected String operatingSystemVersion = BasicTypes.UNASSIGNED_STR;

    // Applications
    protected List<String> appSoftware = new LinkedList<String>();


    public SoftwareDescription() {
        this.operatingSystemType = "";
        this.operatingSystemDistribution = "";
        this.operatingSystemVersion = "";
    }

    public SoftwareDescription(String operatingSystemType, String operatingSystemDistribution, String operatingSystemVersion,
            String imageName, String imageType, HashMap<String, String> imageProperties, List<String> appSoftware) {

        this.operatingSystemType = operatingSystemType;
        this.operatingSystemDistribution = operatingSystemDistribution;
        this.operatingSystemVersion = operatingSystemVersion;
        this.appSoftware = appSoftware;
    }

    public String getOperatingSystemType() {
        return operatingSystemType;
    }

    public void setOperatingSystemType(String osType) {
        this.operatingSystemType = osType;
    }

    public String getOperatingSystemDistribution() {
        return operatingSystemDistribution;
    }

    public void setOperatingSystemDistribution(String osDistribution) {
        this.operatingSystemDistribution = osDistribution;
    }

    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(String osVersion) {
        this.operatingSystemVersion = osVersion;
    }

    public List<String> getAppSoftware() {
        return appSoftware;
    }

    public void setAppSoftware(List<String> appSoftware) {
        this.appSoftware = appSoftware;
    }

}
