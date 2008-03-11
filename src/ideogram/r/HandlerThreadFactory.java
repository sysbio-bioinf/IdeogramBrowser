/*
 * File:	HandlerThreadFactory.java
 * 
 * Created: 	06.03.2008
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory that adds a {@link UncaughtExceptionHandler} object as
 * UncaughtExceptionHandler.
 * 
 * @author Ferdinand Hofherr
 * 
 */
public class HandlerThreadFactory implements ThreadFactory {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    public Thread newThread(Runnable r) {
	Thread t = new Thread(r);
	t.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
	return t;
    }

}
