/*
 * File:	DataSetNameInputField.java
 * Created: 15.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui.inputwidgets;

import ideogram.r.exceptions.InvalidInputException;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.event.FocusEvent;
import java.lang.reflect.Field;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RDataSetNameInputField extends AbstractRInputField {

    private String defaultValue;

    public RDataSetNameInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper) {
        super(mandatory, field, wrapper);
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

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#validateInput()
     */
    @Override
    public boolean validateInput() throws InvalidInputException {
        // Is very hard to validate!
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getText().length() == 0;
    }

    public void focusGained(FocusEvent e) {
        setMessageDisplayText("Please enter the name of the data set you intend to use!");
    }

    public void focusLost(FocusEvent e) {
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

}
