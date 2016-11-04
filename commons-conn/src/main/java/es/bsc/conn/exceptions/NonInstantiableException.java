package es.bsc.conn.exceptions;

public class NonInstantiableException extends RuntimeException {

    /**
     * Exceptions Version UID are 2L in all Runtime
     */
    private static final long serialVersionUID = 2L;


    /**
     * Instantiates a new exception with a given non-instantiable class name
     * @param className
     */
    public NonInstantiableException(String className) {
        super("Class " + className + " can not be instantiated.");
    }

}
