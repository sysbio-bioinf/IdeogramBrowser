/*
 * File:	RException.java
 * Created: 29.11.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

/**
 * Parentclass for all exceptions related to IdeogramBrowser's R interface.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RException extends Exception {

    // TODO check about that version UID field!
    
    public RException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public RException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public RException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public RException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
