/*
 * File:	RNumericInputField.java
 * Created: 15.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui.inputwidgets;

import ideogram.r.RController;
import ideogram.r.exceptions.InvalidInputException;
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
    
    String defaultValue;
    
    /**
     * 
     * TODO INSERT DOCUMENTATION HERE!
     *
     * @param mandatory
     * @param field
     * @param wrapper
     */
    public RNumericInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper) {
        this(mandatory, field, wrapper, false);
    }
    
    /**
     * 
     * TODO INSERT DOCUMENTATION HERE!
     *
     * @param mandatory
     * @param field
     * @param wrapper
     * @param expectVector
     */
    public RNumericInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper, boolean expectVector) {
        super(mandatory, field, wrapper, expectVector);
        
        try {
            defaultValue = (String)field.get(getWrapper());
            setText(defaultValue);
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
            getField().set(getWrapper(), defaultValue);
            setText(defaultValue);
        } catch (IllegalArgumentException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates the user input into the {@link RNumericInputField}. Valid user
     * input consists either of:
     * <ul>
     *  <li>a single numeric value, such as 1, or 1.0.</li>
     *  <li>a comma separated list of numeric values, if the underlying 
     *      R function expects an vector</li>
     * </ul>   
     * If a vector is expected, its items may be named (e.g. d=6).
     */
    @Override
    public boolean validateInput() {
        String[] splitInput = getText().split(",");
        String[] tmp;

        for (String s: splitInput) {
            /* Check for each entry of splitInput if it is a valid numeric
             * value. If splitInput contains a "="-sign, check whether the
             * substring after it is a valid double.
             */
            tmp = s.split("=");
            if (tmp.length == 1) {
                if (!RController.isValidRNumeric(tmp[0])) {
                    return false;
                }
            }
            else {
                if (!RController.isValidRIdentifier(tmp[0]) && 
                        !RController.isValidRNumeric(tmp[1])) {
                    return false;
                }
            }
        }
        return true;
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
                getField().set(getWrapper(), getText());
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
