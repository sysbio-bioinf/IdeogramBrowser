/*
 * File:	JRIVersionException.java
 * Created: 29.11.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

/**
 * Thrown when the version of JRI.jar and the native JRI library do not match.
 *
 * @author Ferdinand Hofherr
 *
 */
public class JRIVersionException extends RException {

    public JRIVersionException() {
        super();
    }

    public JRIVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JRIVersionException(String message) {
        super(message);
    }

    public JRIVersionException(Throwable cause) {
        super(cause);
    }
}
