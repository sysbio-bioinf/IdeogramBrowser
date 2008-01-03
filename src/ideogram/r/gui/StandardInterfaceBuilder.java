/*
 * File:	InterfaceBuilder.java
 * Created: 11.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.exceptions.InvalidInputException;
import ideogram.r.gui.inputwidgets.RBoolInputField;
import ideogram.r.gui.inputwidgets.RDataSetNameInputField;
import ideogram.r.gui.inputwidgets.RInputWidget;
import ideogram.r.gui.inputwidgets.RNumericInputField;
import ideogram.r.gui.inputwidgets.RStringInputField;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * This builder creates interface panels for R libraries. If an library provides
 * more than one analysis function, each function will receive its own interface
 * as a tab on a {@link JTabbedPane}.
 *
 * @author Ferdinand Hofherr
 *
 */
public class StandardInterfaceBuilder implements RInterfaceBuilder {

    private static final int GLOB_HORIZ_GAP = 10;
    private static final String PERFORM_ANALYSIS = "Perform analysis";
    private static final String RESET_FIELDS = "Reset fields";

    private RInterfacePanel interfacePanel;
    private JTabbedPane analysisInterface;
    private JPanel inputFields, buttonPanel;
    private MessageDisplay mdp;
    private RLibraryWrapper wrapper;

    public StandardInterfaceBuilder(MessageDisplay mdp, RLibraryWrapper wrapper) {
        this.mdp = mdp;
        this.wrapper = wrapper;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#buildRBoolInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRBoolInput(String label, Field field, boolean mandatory) {
        JLabel l = new JLabel(label);

        RBoolInputField iField = new RBoolInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#buildRDsNameInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRDsNameInput(String label, Field field, boolean mandatory) {
        JLabel l = new JLabel(label);
        RDataSetNameInputField iField = 
            new RDataSetNameInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#buildRNumericInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRNumericInput(String label, Field field, boolean mandatory) {
        JLabel l = new JLabel(label);        
        RNumericInputField iField = 
            new RNumericInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#buildRStringInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRStringInput(String label, Field field, boolean mandatory) {
        JLabel l = new JLabel(label);
        RStringInputField iField =
            new RStringInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /*
     * Add an input field to the inputFields panel.
     */
    private void addInputField(JLabel label, Component iField) {
        JPanel p = new JPanel(new GridLayout(1,2,GLOB_HORIZ_GAP,0));
        p.add(label);
        p.add(iField);
        inputFields.add(p);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#createAnalysisInterface(java.lang.String)
     */
    public void createAnalysisInterface(String methodName) {
        inputFields = new JPanel();
        inputFields.setLayout(new BoxLayout(inputFields, BoxLayout.PAGE_AXIS));
        analysisInterface.addTab(methodName, inputFields);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#createNewRInterfacePanel(ideogram.r.rlibwrappers.RLibraryWrapper, ideogram.r.gui.MessageDisplay)
     */
    public void createNewRInterfacePanel(RLibraryWrapper model) {
        interfacePanel = new RInterfacePanel(model, mdp);
        
        analysisInterface = new JTabbedPane();
        interfacePanel.addAnalysisInterface(analysisInterface);
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        interfacePanel.add(buttonPanel);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#getRInterfacePanel()
     */
    public Component getRInterfacePanel() {
        return interfacePanel;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#getMessageDisplay()
     */
    public MessageDisplay getMessageDisplay() {
        return mdp;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#setMessageDisplay(ideogram.r.gui.MessageDisplay)
     */
    public void setMessageDisplay(MessageDisplay mdp) {
        this.mdp = mdp;
    }

    public void buildPerformButton(String funcname) {
        JButton b = new JButton("Perform analysis");
        b.setActionCommand(PERFORM_ANALYSIS);
        b.addActionListener(new ButtonActionListener(funcname));
        buttonPanel.add(b);
        buttonPanel.add(Box.createHorizontalGlue());
    }

    public void buildResetButton() {
        JButton b = new JButton("Reset to defaults");
        b.setActionCommand(RESET_FIELDS);
        b.addActionListener(new ButtonActionListener());
        buttonPanel.add(b);
    }
    
    private class ButtonActionListener implements ActionListener {
        String funcname;
        
        public ButtonActionListener() {
            funcname = null;
        }
        
        public ButtonActionListener(String funcname) {
            this.funcname = funcname;
        }

        public void actionPerformed(ActionEvent e) {
            // Cast OK, as analysis interface contains only JPanels.
            JPanel p = (JPanel)analysisInterface.getSelectedComponent();
            RInputWidget ri;
            for (Component c: p.getComponents()) {
                // Cast OK, as each subcomponent of p is a JPanel again!
                ri = findInputWidget((JPanel)c);
                // TODO This can be done nicer! The second Component is always the input widget.
                if (ri != null) {
                    if (e.getActionCommand().equalsIgnoreCase(RESET_FIELDS)) {
                        ri.resetToDefault();
                    }
                    else {
                        try {
                            ri.validateInput();
                        } catch (InvalidInputException e1) {
                            return;
                        }
                    }
                }
            }
            if (e.getActionCommand().equalsIgnoreCase(PERFORM_ANALYSIS) &&
                    funcname != null) {
                // TODO call analysis method.
            }
        }
        
        private RInputWidget findInputWidget(JPanel p) {
            for (Component c: p.getComponents()) {
                if (c instanceof RInputWidget) {
                    return (RInputWidget)c;
                }
            }
            return null;
        }
    }
}
