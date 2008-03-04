package ideogram.r.gui;

import java.util.Observer;

/**
 * Classes that implement this interface are able to display status messages.
 *
 * @author Ferdinand Hofherr
 *
 */
public interface MessageDisplay {
    
    /**
     * Set the message that shall be displayed when nothing else remains to say.
     *
     * @param msg
     */
    public void setDefaultMessage(String msg);
    
    /**
     * Display the default message.
     *
     */
    public void displayDefaultMessage();
    
    /**
     * Display the given message.
     *
     * @param msg
     */
    public void displayMessage(String msg);
    
    /**
     * Add the observer obs to this model.
     *
     * @param obs
     */
    public void addObserver(Observer obs);
    
    /**
     * Get the message that shall be displayed at the moment.
     *
     * @return The message to display.
     */
    public String getCurrentMessage();
}
