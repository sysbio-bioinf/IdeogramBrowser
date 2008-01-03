/*
 * File:	RBoolInputField.java
 * Created: 10.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui.inputwidgets;

import ideogram.r.gui.MessageDisplay;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JComboBox;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RBoolInputField extends JComboBox implements RInputWidget {

    private static final String[] VALUES = new String[] {"TRUE", "FALSE", "NA"};
    
    private String defaultValue;
    private boolean mandatory;
    private Field field;
    private RLibraryWrapper wrapper;
    private MessageDisplay mdp;
    
    /**
     * Select one of the three possible R boolean values.
     *
     * @param mandatory
     * @param paramNo
     * @param accu
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public RBoolInputField(boolean mandatory, Field field,
            RLibraryWrapper wrapper) {
        super(VALUES);
        this.mandatory = mandatory;
        this.field = field;
        this.wrapper = wrapper;
        
        try {
            this.defaultValue = (String)field.get(wrapper);
            selectDefaultValue();
        } catch (IllegalArgumentException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            setMessageDisplayText(e.getLocalizedMessage());
            e.printStackTrace();
        }
        
        setEditable(false);
        addActionListener(new RBoolActionListener());
        resetToDefault();
    }
    
    /*
     * Select the default value. Mark the box as not mandatory. If there is 
     * no defaultvalue, or the default value is invalid, set defaultValue to 
     * "" and mark the box as mandatory. No item will be selected in the combo
     * box.
     */
    private void selectDefaultValue() {
        for (int i = 0; i < VALUES.length; i++) {
            if (defaultValue.equals(VALUES[i])) {
                setSelectedIndex(i);
                // A boolean field with an default value can't be mandatory
                mandatory = false;
                return;
            }
        }
        
        // Only reached when the default value does not match any entry in 
        // VALUES.
        defaultValue = "";
        setSelectedIndex(-1);
        mandatory = true;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.AbstractRInputField#resetToDefault()
     */
    public void resetToDefault() {
        try {
            getField().set(getWrapper(), defaultValue);
            selectDefaultValue();
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
    public boolean validateInput() {
        return getSelectedIndex() != -1;
    }

    public boolean isEmpty() {
        // Concerned empty on invalid input.
        return validateInput();
    }
    
    protected Field getField() {
        return field;
    }
    
    protected RLibraryWrapper getWrapper() {
        return wrapper;
    }

    /*
     * Set the MessageDisplay to the given text.
     */
    private void setMessageDisplayText(String text) {
        if (getMessageDisplay() != null) {
            getMessageDisplay().setMessage(text);
        }
        else {
            System.out.println(text);
        }
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.inputwidgets.RInputWidget#getMessageDisplay()
     */
    public MessageDisplay getMessageDisplay() {
        return mdp;
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
    
    private class RBoolActionListener implements ActionListener {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (validateInput()) {
                try {
                    getField().set(getWrapper(), getSelectedItem());
                    setBackground(Color.WHITE);
                } catch (IllegalArgumentException e1) {
                    setMessageDisplayText(e1.getLocalizedMessage());
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    setMessageDisplayText(e1.getLocalizedMessage());
                    e1.printStackTrace();
                }
            }            
        }
    }
}
