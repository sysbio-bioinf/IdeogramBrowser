/*
 * File:	RMainLoopModel.java
 * Created: 28.11.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import java.util.Observable;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 * Model for the R main loop. Observers of this model will be notified about
 * R's status and its console output.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RMainLoopModel extends Observable implements RMainLoopCallbacks {

    private final int R_CONSOLE_MAXCAP = 1000000; // 1000 kb
    
    private RConsoleBuffer rConsole;
    private boolean rBusy;
    
    public RMainLoopModel() {
        rConsole = new RConsoleBuffer(R_CONSOLE_MAXCAP); 
        rBusy = false;
    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rBusy(org.rosuda.JRI.Rengine, int)
     */
    public void rBusy(Rengine re, int which) {
        switch (which) {
            case 0: rBusy = false; break;
            case 1: rBusy = true; break;
        }
        setChanged();
        notifyObservers();
    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rChooseFile(org.rosuda.JRI.Rengine, int)
     */
    public String rChooseFile(Rengine arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rFlushConsole(org.rosuda.JRI.Rengine)
     */
    public void rFlushConsole(Rengine arg0) {
        //rConsole.flush();
        setChanged();
        notifyObservers();
    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rLoadHistory(org.rosuda.JRI.Rengine, java.lang.String)
     */
    public void rLoadHistory(Rengine arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rReadConsole(org.rosuda.JRI.Rengine, java.lang.String, int)
     */
    public String rReadConsole(Rengine arg0, String arg1, int arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rSaveHistory(org.rosuda.JRI.Rengine, java.lang.String)
     */
    public void rSaveHistory(Rengine arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rShowMessage(org.rosuda.JRI.Rengine, java.lang.String)
     */
    public void rShowMessage(Rengine re, String text) {
        rConsole.insert(text);
        setChanged();
        notifyObservers();
    }

    /* (non-Javadoc)
     * @see org.rosuda.JRI.RMainLoopCallbacks#rWriteConsole(org.rosuda.JRI.Rengine, java.lang.String, int)
     */
    public void rWriteConsole(Rengine re, String text, int oType) {
        rConsole.insert(text);
        //System.out.println(rConsole.noChars());
        // Mark RMainLoopModel as changed and notify observers.
        setChanged(); 
        notifyObservers();
    }
    
    /**
     * Get a String representing the contents of the current RConsole.
     *
     * @return String with the contents of the RConsole.
     */
    public String getRConsole() {
        return rConsole.toString();
    }
    
    public boolean isBusy() {
        return rBusy;
    }
}
