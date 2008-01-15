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
 * Input field for R strings. The surrounding single quotes (') needed by R
 * will be removed, before the string gets displayed. They will be added, before
 * the string is stored into the corresponding public field in the wrapper 
 * class.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RStringInputField extends AbstractRInputField {

    private final String MDP_TEXT = "This field only accepts Strings!";
    private String defaultValue; // stored with ' quotes!
    
    
    public RStringInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper) {
        super(mandatory, field, wrapper);
        try {
            defaultValue = (String)field.get(wrapper);
            setText(removeQuotes(defaultValue));
        } catch (IllegalArgumentException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    /*
     * Remove the surrounding single quotes (') from the String s.
     */
    private String removeQuotes(String s) {
        int firstInd = 0;
        int lastInd = s.length() - 1;
        char firstChar = s.charAt(firstInd);
        char lastChar = s.charAt(lastInd);
        if (firstChar == '\'' || firstChar == '"') firstInd++;
        if (lastChar == '\'' || lastChar == '"') lastInd--;
        return s.substring(firstInd, lastInd + 1);
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
        setText(removeQuotes(defaultValue));
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#validateInput()
     */
    @Override
    public boolean validateInput() {
        if(isMandatory() && getText().length() == 0) return false;
        // TODO think about string vectors!
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
                getField().set(getWrapper(), "'" + getText() + "'");
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
