/*
 * File:	InterfaceBuilder.java
 * Created: 11.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.awt.GridLayout;
import java.lang.reflect.Field;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class StandardInterfaceBuilder implements RInterfacePanelBuilder {
    
    private static int GLOB_HORIZ_GAP = 10;
    private RInterfacePanel interfPanel;
    private JTabbedPane analysisInterface;
    private JPanel inputFields;
    private MessageDisplay mdp;
    private RLibraryWrapper wrapper;
    
    public StandardInterfaceBuilder(MessageDisplay mdp, RLibraryWrapper wrapper) {
        this.mdp = mdp;
        this.wrapper = wrapper;
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRBoolInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRBoolInput(String label, Field field, boolean mandatory) {
        JLabel l = new JLabel(label);

        RBoolInputField iField = new RBoolInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRDsNameInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRDsNameInput(String label, Field field,
            boolean mandatory) {
        
        JLabel l = new JLabel(label);
        RDataSetNameInputField iField = 
            new RDataSetNameInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRNumericInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRNumericInput(String label, Field field,
            boolean mandatory) {
        JLabel l = new JLabel(label);        
        RNumericInputField iField = 
            new RNumericInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        addInputField(l, iField);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRStringInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRStringInput(String label, Field field,
            boolean mandatory) {
        JLabel l = new JLabel(label);
        RStringInputField iField =
            new RStringInputField(mandatory, field, wrapper);
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
     * @see ideogram.r.gui.RInterfacePanelBuilder#createAnalysisInterface(java.lang.String)
     */
    public void createAnalysisInterface(String methodName) {
        inputFields = new JPanel();
        inputFields.setLayout(new BoxLayout(inputFields, BoxLayout.PAGE_AXIS));
        analysisInterface.addTab(methodName, inputFields);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#createNewRInterfacePanel(ideogram.r.rlibwrappers.RLibraryWrapper, ideogram.r.gui.MessageDisplay)
     */
    public void createNewRInterfacePanel(RLibraryWrapper model) {
        interfPanel = new RInterfacePanel(model, mdp);
        analysisInterface = new JTabbedPane();
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#getRInterfacePanel()
     */
    public Component getRInterfacePanel() {
        interfPanel.addAnalysisInterface(analysisInterface);
        return interfPanel;
    }

    public MessageDisplay getMessageDisplay() {
        return mdp;
    }

    public void setMessageDisplay(MessageDisplay mdp) {
        this.mdp = mdp;
    }

}
