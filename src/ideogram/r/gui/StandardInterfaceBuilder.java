/*
 * File:	InterfaceBuilder.java
 * Created: 11.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.exceptions.InvalidInputException;
import ideogram.r.exceptions.RException;
import ideogram.r.gui.inputwidgets.RBoolInputField;
import ideogram.r.gui.inputwidgets.RDataSetNameInputField;
import ideogram.r.gui.inputwidgets.RInputWidget;
import ideogram.r.gui.inputwidgets.RNumericInputField;
import ideogram.r.gui.inputwidgets.RStringInputField;
import ideogram.r.rlibwrappers.RAnalysisWrapper;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    
    //private int noInputFields;

    public StandardInterfaceBuilder(MessageDisplay mdp, RLibraryWrapper wrapper) {
        this.mdp = mdp;
        this.wrapper = wrapper;
        //noInputFields = 0;
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
        //noInputFields++;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#createAnalysisInterface(java.lang.String)
     */
    public void createAnalysisInterface(String methodName) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        
        inputFields = new JPanel();
        inputFields.setLayout(new GridLayout(0, 3, GLOB_HORIZ_GAP, 0));
        p.add(inputFields);
        
        p.add(Box.createVerticalGlue());
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        p.add(buttonPanel);

        analysisInterface.addTab(methodName, p);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#createNewRInterfacePanel(ideogram.r.rlibwrappers.RLibraryWrapper, ideogram.r.gui.MessageDisplay)
     */
    public void createNewRInterfacePanel(RAnalysisWrapper model) {
        interfacePanel = new RInterfacePanel(model, mdp);
        analysisInterface = new JTabbedPane();
        interfacePanel.addAnalysisInterface(analysisInterface);        
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

    public void buildPerformButton(Method analysisFunction) {
        JButton b = new JButton("Perform analysis");
        b.setActionCommand(PERFORM_ANALYSIS);
        b.addActionListener(new ButtonActionListener(analysisFunction));
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
        Method analysisFunction;
        
        public ButtonActionListener() {
            analysisFunction = null;
        }
        
        public ButtonActionListener(Method analysisFunction) {
            this.analysisFunction = analysisFunction;
        }

        public void actionPerformed(ActionEvent e) {
            // Cast OK, as analysis interface contains only JPanels.
            JPanel p = (JPanel)analysisInterface.getSelectedComponent();
            p = (JPanel)p.getComponent(0);
            RInputWidget ri;
            for (Component c: p.getComponents()) {
                // Cast OK, as each subcomponent of p is a JPanel again!
                ri = findInputWidget((JPanel)c);
                if (e.getActionCommand().equalsIgnoreCase(RESET_FIELDS)) {
                    ri.resetToDefault();
                }
                else {
                    try {
                        ri.validateInput();
                    } catch (InvalidInputException e1) {
                        if (mdp != null) {
                            mdp.displayMessage(e1.getLocalizedMessage());
                        }
                        return; // If some input is invalid the action can't be performed.
                    }
                }
            }
            if (e.getActionCommand().equalsIgnoreCase(PERFORM_ANALYSIS) &&
                    analysisFunction != null) {
                try {
                    analysisFunction.invoke(wrapper, (Object[])null);
//                    try { // JUST A TEST
//                        wrapper.getResult();
//                    } catch (RException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    } // REMOVE ABOVE LINES AFTER TEST
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    if (mdp != null) {
                        mdp.displayMessage(e1.getCause().getLocalizedMessage());
                    }
                    else {
                        System.out.println(e1.getCause().getLocalizedMessage());
                    }
                    //e1.printStackTrace();
                }
            }
        }
        
        private RInputWidget findInputWidget(JPanel p) {
            return (RInputWidget) p.getComponent(1);
        }
    }
}
