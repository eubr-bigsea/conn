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


    private BasicTypes() {
        throw new NonInstantiableException("Loggers should not be instantiated");
    }
    
}
