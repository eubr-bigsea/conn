package es.bsc.conn.exceptions;

public class ConnException extends Exception {

    /**
     * Exception Version UID are 2L in all Runtime
     */
    private static final long serialVersionUID = 2L;


    public ConnException(String message) {
        super(message);
    }

    public ConnException(Exception e) {
        super(e);
    }

    public ConnException(String msg, Exception e) {
        super(msg, e);
    }

}
