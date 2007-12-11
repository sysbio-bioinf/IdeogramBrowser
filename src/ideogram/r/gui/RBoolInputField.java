/*
 * File:	RBoolInputField.java
 * Created: 10.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RBoolInputField extends AbstractRInputField implements FocusListener {

    private String defaultValue;
    
    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @param mandatory
     * @param paramNo
     * @param accu
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public RBoolInputField(boolean mandatory, Field f, RLibraryWrapper wrapper) {
        super(mandatory, f, wrapper);
        try {
            this.defaultValue = (String)f.get(wrapper);
        } catch (IllegalArgumentException e) {
            setMdpText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMdpText(e.getLocalizedMessage());
            e.printStackTrace();
        }
        resetToDefault();
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        setMdpText("This field only accepts TRUE, FALSE, or NA!");
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        if (validateInput()) {
            try {
                getField().set(getWrapper(), this.getText());
            } catch (IllegalArgumentException e1) {
                setMdpText(e1.getLocalizedMessage());
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                setMdpText(e1.getLocalizedMessage());
                e1.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#resetToDefault()
     */
    @Override
    public void resetToDefault() {
        try {
            getField().set(getWrapper(), defaultValue);
            this.setText(defaultValue);
        } catch (IllegalArgumentException e) {
            setMdpText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMdpText(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#validateInput()
     */
    @Override
    public boolean validateInput() {
        if (!(this.getText().equals("FALSE") ||
                this.getText().equals("TRUE") ||
                this.getText().equals("NA"))) {
            setMdpText("Set this field to either " +
                		"'TRUE', 'FALSE', or 'NA'!");
            this.setBackground(Color.RED);
            return false;
        }
        return true;
    }

}
