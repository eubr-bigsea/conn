package es.bsc.conn.types;

import java.util.LinkedList;
import java.util.List;

public class SoftwareDescription {
    public static final String UNASSIGNED_STR 	= "[unassigned]";
    // Operating System
    protected String operatingSystemType = UNASSIGNED_STR;
    protected String operatingSystemDistribution = UNASSIGNED_STR;
    protected String operatingSystemVersion = UNASSIGNED_STR;
    // Applications
    protected List<String> appSoftware = new LinkedList<String>();

    public SoftwareDescription(){

    }
}
