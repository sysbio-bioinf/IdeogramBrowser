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
        // TODO Auto-generated constructor stub
    }

    public JRIVersionException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public JRIVersionException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public JRIVersionException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
