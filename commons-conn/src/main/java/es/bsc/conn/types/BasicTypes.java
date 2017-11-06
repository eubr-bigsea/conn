package es.bsc.conn.types;

import es.bsc.conn.exceptions.NonInstantiableException;

/**
 * Basic default types for Connectors Commons
 *
 */
public class BasicTypes {

    // Types
    public static final int UNASSIGNED_INT = -1;
    public static final String UNASSIGNED_STR = "[unassigned]";
    public static final float UNASSIGNED_FLOAT = (float) -1.0;
    public static final int ZERO_INT = 0;
    public static final int ONE_INT = 1;

    public static final String CPU_TYPE = "CPU";
    public static final String GPU_TYPE = "GPU";
    public static final String FPGA_TYPE = "FPGA";
    public static final String OTHER_TYPE = "OTHER";
    
    /**
     * Unassigned value for processor type
     */
    public static final String UNASSIGNED_PROCESSOR_TYPE = CPU_TYPE;
    
    private BasicTypes() {
        throw new NonInstantiableException("Loggers should not be instantiated");
    }
    
}
