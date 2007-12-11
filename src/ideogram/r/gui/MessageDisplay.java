package ideogram.r.gui;

/**
 * Classes that implement this interface are able to display status messages.
 *
 * @author Ferdinand Hofherr
 *
 */
public interface MessageDisplay {
    
    /**
     * Display the default message.
     *
     */
    public void setDefaultMessage();
    
    /**
     * Display the given message.
     *
     * @param msg
     */
    public void setMessage(String msg);
}
