/*
 * File:	RNumericInputField.java
 * Created: 15.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui.inputwidgets;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.lang.reflect.Field;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RNumericInputField extends AbstractRInputField {

    private static final String MDP_TEXT = 
        "This fields accepts only numeric values!";
    
    double defaultValue;
    private double currentInput; // will be set by the validateInput() method!!
    
    public RNumericInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper) {
        super(mandatory, field, wrapper);
        
        try {
            defaultValue = field.getDouble(getWrapper());
            setText(Double.toString(defaultValue));
        } catch (IllegalArgumentException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return validateInput(); // invalid input is considered as empty
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#resetToDefault()
     */
    @Override
    public void resetToDefault() {
        try {
            getField().setDouble(getWrapper(), defaultValue);
            setText(Double.toString(defaultValue));
        } catch (IllegalArgumentException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#validateInput()
     * 
     * Sets the currentInput field as a side effect!
     */
    @Override
    public boolean validateInput() {
        try {
            currentInput = Double.parseDouble(getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        setMessageDisplayText(MDP_TEXT);

    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        if (validateInput()) {
            try {
                setBackground(Color.WHITE);
                getField().setDouble(getWrapper(), currentInput);
            } catch (IllegalArgumentException e1) {
                setMessageDisplayText(e1.getLocalizedMessage());
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                setMessageDisplayText(e1.getLocalizedMessage());
                e1.printStackTrace();
            }
        }
        else {
            setMessageDisplayText(MDP_TEXT);
            setBackground(Color.RED);
        }
    }

}
