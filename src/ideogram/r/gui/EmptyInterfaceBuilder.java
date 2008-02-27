/*
 * File: EmptyInterfaceBuilder.java Created: 10.12.2007 Author: Ferdinand
 * Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.rlibwrappers.RAnalysisWrapper;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Interface builder which builds empty interfaces. This interface builder 
 * is needed, when there is still no R library selected, and thus no interface
 * needs to be built. It can be used, to display some default information.
 * 
 * @author Ferdinand Hofherr
 */
public class EmptyInterfaceBuilder implements RInterfaceBuilder {

    private RInterfacePanel interfacePanel;
    private MessageDisplay mdp;

    /**
     * Pass null for mdp if there does not exist one. However this might lead 
     * to undefined results. 
     *
     * @param mdp
     */
    public EmptyInterfaceBuilder(MessageDisplay mdp) {
        this.mdp = mdp;
        createNewRInterfacePanel(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRBoolInput(java.lang.String,
     *      int)
     */
    public void buildRBoolInput(String label, Field f, boolean mandatory) {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRDsNameInput(java.lang.String,
     *      java.lang.String)
     */
    public void buildRDsNameInput(String label, Field f, boolean mandatory) {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRNumericInput(java.lang.String,
     *      double)
     */
    public void buildRNumericInput(String label, Field f, boolean mandatory) {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRStringInput(java.lang.String,
     *      java.lang.String)
     */
    public void buildRStringInput(String label, Field f, boolean mandatory) {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#createAnalysisInterface(java.lang.String)
     */
    public void createAnalysisInterface(String methodName) {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#createNewRInterfacePanel(ideogram.r.rlibwrappers.RLibraryWrapper)
     */
    public void createNewRInterfacePanel(RAnalysisWrapper wrapper) {
        JPanel p = new JPanel();
        p.add(new JLabel("No library selected!"));
        interfacePanel = new RInterfacePanel(wrapper, mdp);
        interfacePanel.addAnalysisInterface(p);
    }

    /* (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#getRInterfacePanel()
     */
    public Component getRInterfacePanel() {
        return interfacePanel;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#setMessageDisplay(ideogram.r.gui.MessageDisplay)
     */
    public void setMessageDisplay(MessageDisplay mdp) {
        this.mdp = mdp;
    }
    
    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#getMessageDisplay()
     */
    public MessageDisplay getMessageDisplay() {
        return mdp;
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#buildPerformButton()
     */
    public void buildPerformButton(Method analysisFunction) {
        // nothing to do        
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfaceBuilder#buildResetButton()
     */
    public void buildResetButton() {
        // nothing to do        
    }

}
