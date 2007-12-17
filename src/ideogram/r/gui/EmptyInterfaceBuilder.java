/*
 * File: EmptyInterfaceBuilder.java Created: 10.12.2007 Author: Ferdinand
 * Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * INSERT DOCUMENTATION HERE!
 * 
 * @author Ferdinand Hofherr
 */
public class EmptyInterfaceBuilder implements RInterfacePanelBuilder {

    private RInterfacePanel interfacePanel;
    private MessageDisplay mdp;

    /**
     * Pass null for mdp if there does not exist one.
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
        // nothing todo
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRDsNameInput(java.lang.String,
     *      java.lang.String)
     */
    public void buildRDsNameInput(String label, Field f,
            boolean mandatory) {
        // nothing todo
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRNumericInput(java.lang.String,
     *      double)
     */
    public void buildRNumericInput(String label, Field f,
            boolean mandatory) {
        // nothing todo
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRStringInput(java.lang.String,
     *      java.lang.String)
     */
    public void buildRStringInput(String label, Field f,
            boolean mandatory) {
        // nothing todo
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#createAnalysisInterface(java.lang.String)
     */
    public void createAnalysisInterface(String methodName) {
        // nothing todo
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#createNewRInterfacePanel(ideogram.r.rlibwrappers.RLibraryWrapper)
     */
    public void createNewRInterfacePanel(RLibraryWrapper wrapper) {
        JPanel p = new JPanel();
        p.add(new JLabel("No library selected!"));
        interfacePanel = new RInterfacePanel(wrapper, mdp);
        interfacePanel.addAnalysisInterface(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.gui.RInterfacePanelBuilder#getRInterfacePanel()
     */
    public Component getRInterfacePanel() {
        return interfacePanel;
    }

    public void setMessageDisplay(MessageDisplay mdp) {
        this.mdp = mdp;
    }
    
    public MessageDisplay getMessageDisplay() {
        return mdp;
    }

}
