/*
 * File:	AbstractRInputField.java
 * Created: 10.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui.inputwidgets;

import ideogram.r.exceptions.InvalidInputException;
import ideogram.r.gui.MessageDisplay;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JTextField;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public abstract class AbstractRInputField extends JTextField 
implements FocusListener, RInputWidget {
    
    private boolean mandatory;
    private MessageDisplay mdp;
    private Field field;
    private RLibraryWrapper wrapper;
    
    public AbstractRInputField(boolean mandatory, Field field, RLibraryWrapper wrapper) {
        this.mandatory = mandatory;
        this.mdp = null;
        this.field = field;
        this.wrapper = wrapper;
        addFocusListener(this);
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#isMandatory()
     */
    public boolean isMandatory() {
        return mandatory;
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#setMessageDisplay(ideogram.r.gui.MessageDisplay)
     */
    public void setMessageDisplay(MessageDisplay mdp) {
        this.mdp = mdp;
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#getMessageDisplay()
     */
    public MessageDisplay getMessageDisplay() {
        return mdp;
    }
    
    /**
     * Get the {@link RLibraryWrapper}'s public field associated with this 
     * input field.
     *
     * @return
     */
    protected Field getField() {
        return field;
    }
    
    /**
     * Get the {@link RLibraryWrapper} for which this input field provides a
     * part of an interface.
     *
     * @return
     */
    protected RLibraryWrapper getWrapper() {
        return wrapper;
    }
    
    /**
     * Set the {@link MessageDisplay}'s text.
     *
     * @param text
     */
    protected void setMessageDisplayText(String text) {
        if (mdp != null) {
            mdp.setMessage(text);
        }
        else {
            System.out.println(text);
        }
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#validateInput()
     */
    public abstract boolean validateInput() throws InvalidInputException;
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#resetToDefault()
     */
    public abstract void resetToDefault();
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#isEmpty()
     */
    public abstract boolean isEmpty();
}
