package es.bsc.conn.types;

import java.util.LinkedList;
import java.util.List;

public class SoftwareDescription {
    // Operating System
    protected String operatingSystemType = BasicTypes.UNASSIGNED_STR;
    protected String operatingSystemDistribution = BasicTypes.UNASSIGNED_STR;
    protected String operatingSystemVersion = BasicTypes.UNASSIGNED_STR;

    // Image
    protected String imageName;

    // Applications
    protected List<String> appSoftware = new LinkedList<String>();

    public SoftwareDescription(){

    }

    public SoftwareDescription(String osType, String osDistribution, String osVersion, List<String> appSoftware) {
        this.operatingSystemType = osType;
        this.operatingSystemDistribution = osDistribution;
        this.operatingSystemVersion = osVersion;
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

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }

    public List<String> getAppSoftware() {
        return appSoftware;
    }

    public void setAppSoftware(List<String> appSoftware) {
        this.appSoftware = appSoftware;
    }
}
