/*
 * File:	UncaughtExceptionHandler.java
 * 
 * Created:	06.03.2008
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.exceptions.RException;

import javax.swing.JOptionPane;

/**
 * Alternative UncaughtExceptionHandler. Displays a message dialog with the
 * exceptions localized message. If the Exception is not an instance of
 * {@link RException} the stack trace is printed additionally.
 * 
 * @author Ferdinand Hofherr
 * 
 */
public class UncaughtExceptionHandler implements
	Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
	JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
		"Exception", JOptionPane.ERROR_MESSAGE);
	if (!(e instanceof RException)) {
	    e.printStackTrace();
	}
    }

}
