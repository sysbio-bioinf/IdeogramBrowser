/*
 * File: 	RMainLoopModel.java
 * 
 * Created: 	28.11.2007
 * 
 * Author: 	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import java.util.Observable;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 * Model for the R main loop. Observers of this model will be notified about R's
 * status and its console output. This object is shared between the thread
 * running R and IdeogramBrowser's thread.
 * 
 * @author Ferdinand Hofherr
 */
public class RMainLoopModel extends Observable implements RMainLoopCallbacks {

    private final int R_CONSOLE_MAXCAP = 1000000; // 1000 kb

    private RConsoleBuffer rConsole;
    private volatile boolean rBusy = false;

    public RMainLoopModel() {
	rConsole = new RConsoleBuffer(R_CONSOLE_MAXCAP);
    }

    /*
     * It seems that this method is only called, when the R
     * mainloop is started and R is accepting direct user input.
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rBusy(org.rosuda.JRI.Rengine, int)
     */
    public synchronized void rBusy(Rengine re, int which) {
	switch (which) {
	case 0:
	    rBusy = false;
	    break;
	case 1:			// Fall through.
	default:		// Consider any which bigger than one as true.
	    rBusy = true;	
	}
	setChanged();
	notifyObservers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rChooseFile(org.rosuda.JRI.Rengine,
     *      int)
     */
    public synchronized String rChooseFile(Rengine re, int arg1) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rFlushConsole(org.rosuda.JRI.Rengine)
     */
    public synchronized void rFlushConsole(Rengine re) {
	// rConsole.flush();
	setChanged();
	notifyObservers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rLoadHistory(org.rosuda.JRI.Rengine,
     *      java.lang.String)
     */
    public synchronized void rLoadHistory(Rengine re, String arg1) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rReadConsole(org.rosuda.JRI.Rengine,
     *      java.lang.String, int)
     */
    public synchronized String rReadConsole(Rengine re, String arg1, int arg2) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rSaveHistory(org.rosuda.JRI.Rengine,
     *      java.lang.String)
     */
    public synchronized void rSaveHistory(Rengine re, String arg1) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rShowMessage(org.rosuda.JRI.Rengine,
     *      java.lang.String)
     */
    public synchronized void rShowMessage(Rengine re, String text) {
	rConsole.insert(text);
	setChanged();
	notifyObservers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRI.RMainLoopCallbacks#rWriteConsole(org.rosuda.JRI.Rengine,
     *      java.lang.String, int)
     */
    public synchronized void rWriteConsole(Rengine re, String text, int oType) {
	rConsole.insert(text);
	setChanged();
	notifyObservers();
    }

    /**
     * Get a String representing the contents of the current RConsole.
     * 
     * @return String with the contents of the RConsole.
     */
    public synchronized String getRConsole() {
	return rConsole.toString();
    }

    public synchronized boolean isBusy() {
	return rBusy;
    }
}
