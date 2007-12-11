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

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class InterfaceBuilder implements RInterfacePanelBuilder {
    
    private static int GLOB_HORIZ_GAP = 10;
    private RInterfacePanel interfPanel;
    private JPanel inputFields;
     
    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRBoolInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRBoolInput(String label, Field field, boolean mandatory,
            MessageDisplay mdp, RLibraryWrapper wrapper) {
        JPanel p = new JPanel();
        GridLayout gl = new GridLayout(1,2);
        gl.setHgap(GLOB_HORIZ_GAP);
        p.setLayout(gl);
        
        JLabel l = new JLabel(label);
        p.add(l);
        
        RBoolInputField iField = new RBoolInputField(mandatory, field, wrapper);
        iField.setMessageDisplay(mdp);
        p.add(iField);
        
        inputFields.add(p);
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRDsNameInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRDsNameInput(String label, Field field,
            boolean mandatory, MessageDisplay mdp) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRNumericInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRNumericInput(String label, Field field,
            boolean mandatory, MessageDisplay mdp) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#buildRStringInput(java.lang.String, java.lang.reflect.Field, boolean, ideogram.r.gui.MessageDisplay)
     */
    public void buildRStringInput(String label, Field field,
            boolean mandatory, MessageDisplay mdp) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#createAnalysisInterface(java.lang.String)
     */
    public void createAnalysisInterface(String methodName) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#createNewRInterfacePanel(ideogram.r.rlibwrappers.RLibraryWrapper, ideogram.r.gui.MessageDisplay)
     */
    public void createNewRInterfacePanel(RLibraryWrapper model,
            MessageDisplay mdp) {
        interfPanel = new RInterfacePanel(model, mdp);
        inputFields = new JPanel();
        inputFields.setLayout(new BoxLayout(inputFields, BoxLayout.PAGE_AXIS));
    }

    /* (non-Javadoc)
     * @see ideogram.r.gui.RInterfacePanelBuilder#getRInterfacePanel()
     */
    public Component getRInterfacePanel() {
        interfPanel.addInputFields(inputFields);
        return interfPanel;
    }

}
