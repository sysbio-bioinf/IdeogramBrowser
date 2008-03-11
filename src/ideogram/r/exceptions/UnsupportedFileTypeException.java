/*
 * File:	UnsupportedFileTypeException.java
 * 
 * Created: 	26.02.2008
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.exceptions;

/**
 * TODO INSERT DOCUMENTATION HERE!
 * 
 * @author Ferdinand Hofherr
 * 
 */
public class UnsupportedFileTypeException extends RException {

    public UnsupportedFileTypeException() {
	super();
    }

    public UnsupportedFileTypeException(String message, Throwable cause) {
	super(message, cause);
    }

    public UnsupportedFileTypeException(String message) {
	super(message);
    }

    public UnsupportedFileTypeException(Throwable cause) {
	super(cause);
    }

}
