/*
 * File:	RException.java
 * Created: 29.11.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

/**
 * Parent class for all exceptions related to IdeogramBrowser's R interface.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RException extends Exception {

    // TODO check about that version UID field!
    
    public RException() {
        super();
    }

    public RException(String message, Throwable cause) {
        super(message, cause);
    }

    public RException(String message) {
        super(message);
    }

    public RException(Throwable cause) {
        super(cause);
    }
}
