package es.bsc.conn.exceptions;

/**
 * Generic ConnException for all connector' implementations
 *
 */
public class ConnException extends Exception {

    /**
     * Exception Version UID are 2L in all Runtime
     */
    private static final long serialVersionUID = 2L;


    /**
     * Instantiates a new exception from a given message
     * 
     * @param message
     */
    public ConnException(String message) {
        super(message);
    }

    /**
     * Instantiates a nested exception
     * 
     * @param e
     */
    public ConnException(Exception e) {
        super(e);
    }

    /**
     * Instantiates a nested exception with a new message
     * 
     * @param msg
     * @param e
     */
    public ConnException(String msg, Exception e) {
        super(msg, e);
    }

}
