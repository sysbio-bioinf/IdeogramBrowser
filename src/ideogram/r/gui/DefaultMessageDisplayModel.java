/*
 * File:	DefaultMessageDisplayModel.java
 * Created: 04.03.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import java.util.Observable;
import java.util.Observer;

/**
 * Model for all messages that shall be displayed to the user without using one
 * of the predefined dialogs. This implementation makes use of a singleton, as 
 * this is more convenient.
 *
 * @author Ferdinand Hofherr
 *
 */
public class DefaultMessageDisplayModel extends Observable implements MessageDisplay {
    
    private static DefaultMessageDisplayModel instance = 
        new DefaultMessageDisplayModel();
    
    private String defaultMessage, currentMessage;
    
    private DefaultMessageDisplayModel() {
        defaultMessage = currentMessage = "";
    }
    
    /**
     * Get the only instance of this {@link MessageDisplay};
     *
     * @return
     */
    public static MessageDisplay getInstance() { return instance; }

    /* (non-Javadoc)
     * @see ideogram.r.gui.MessageDisplay#setDefaultMessage()
     */
    public synchronized void displayDefaultMessage() {
        currentMessage = defaultMessage;
        setChanged();
        notifyObservers();
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.MessageDisplay#setMessage(java.lang.String)
     */
    public synchronized void displayMessage(String msg) {
        currentMessage = msg;
        setChanged();
        notifyObservers();
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.MessageDisplay#addObserver(java.util.Observer)
     */
    public synchronized void addObserver(Observer obs) {
        super.addObserver(obs);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.MessageDisplay#setDefaultMessage(java.lang.String)
     */
    public synchronized void setDefaultMessage(String msg) {
        this.defaultMessage = msg;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.MessageDisplay#getCurrentMessage()
     */
    public synchronized String getCurrentMessage() {
        return currentMessage;
    }
}
