package es.bsc.conn.loggers;

import es.bsc.conn.exceptions.NonInstantiableException;

/**
 * Loggers' names for Connectors
 *
 */
public class Loggers {

    // Integrated Toolkit
    public static final String IT = "integratedtoolkit";

    // Root connectors logger name
    public static final String CONNECTORS = IT + ".Connectors";
    public static final String CONN = CONNECTORS + ".Conn";

    // Specific connector client loggers for each implementation
    public static final String DUMMY = CONN + ".Dummy";
    public static final String ROCCI = CONN + ".Rocci";
    public static final String JCLOUDS = CONN + ".JClouds";
    public static final String DOCKER = CONN + ".Docker";
    public static final String MESOS = CONN + ".Mesos";
    public static final String VMM = CONN + ".VMM";
    public static final String SLURM = CONN + ".SLURM";

    private Loggers() {
        throw new NonInstantiableException("Loggers should not be instantiated");
    }

}
