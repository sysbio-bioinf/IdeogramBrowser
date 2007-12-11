/*
 * File:	AbstractRInputField.java
 * Created: 10.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JTextField;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public abstract class AbstractRInputField extends JTextField {
    
    private boolean mandatory;
    private MessageDisplay mdp;
    private Field field;
    private RLibraryWrapper wrapper;
    
    public AbstractRInputField(boolean mandatory, Field field, RLibraryWrapper wrapper) {
        this.mandatory = mandatory;
        this.mdp = null;
        this.field = field;
        this.wrapper = wrapper;
    }
    
    /**
     * Reference to a class, which is able to display status messages. Set
     * to null, if no such class exists.
     *
     * @param mdp
     */
    public void setMessageDisplay(MessageDisplay mdp) {
        this.mdp = mdp;
    }
    
    public MessageDisplay getMessageDisplay() {
        return mdp;
    }
    
    protected Field getField() {
        return field;
    }
    
    protected RLibraryWrapper getWrapper() {
        return wrapper;
    }
    
    protected void setMdpText(String text) {
        if (mdp != null) {
            mdp.setMessage(text);
        }
    }
    
    public abstract boolean validateInput() throws InvalidInputException;
    
    public abstract void resetToDefault();
}
