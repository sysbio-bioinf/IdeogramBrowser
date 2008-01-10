/*
 * File:	RStringInputField.java
 * Created: 15.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui.inputwidgets;

import ideogram.r.RController;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RStringInputField extends AbstractRInputField {

    private final String MDP_TEXT = "This field only accepts Strings!";
    private String defaultValue;
    
    
    public RStringInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper) {
        super(mandatory, field, wrapper);
        try {
            defaultValue = (String) field.get(wrapper);
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
        return getText().length() == 0;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#resetToDefault()
     */
    @Override
    public void resetToDefault() {
        try {
            getField().set(getWrapper(), defaultValue);
        } catch (IllegalArgumentException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        }
        setText(defaultValue);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#validateInput()
     */
    @Override
    public boolean validateInput() {
        if(isMandatory() && getText().length() == 0) return false;
        // TODO think about string vectors!
        if (!RController.isValidRString(getText())) return false;
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
            setBackground(Color.WHITE);
            try {
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
            setBackground(Color.RED);
            setMessageDisplayText(MDP_TEXT);
        }
    }

}
