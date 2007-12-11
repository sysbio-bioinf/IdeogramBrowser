package ideogram.r.gui;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.lang.reflect.Field;

public interface RInterfacePanelBuilder {

    /**
     * Tell the Builder that it shall start to create a new Interface panel.
     *
     * @param model
     * @param mdp TODO
     */
    public void createNewRInterfacePanel(RLibraryWrapper model, MessageDisplay mdp);

    /**
     * Create an interface to an new analysis method. All successive calls to
     * build* methods will affect this interface until createAnalysisInterface
     * is called again.
     *
     * @param methodName
     */
    public void createAnalysisInterface(String methodName);
    
    /**
     * 
     * INSERT DOCUMENTATION HERE!
     *
     * @param label
     * @param mandatory TODO
     * @param mdp TODO
     * @param wrapper TODO
     * @param defaultValue
     * @param paramNo TODO
     */
    public void buildRBoolInput(String label, Field field, 
            boolean mandatory, MessageDisplay mdp, RLibraryWrapper wrapper);
    
    /**
     * 
     * INSERT DOCUMENTATION HERE!
     *
     * @param label
     * @param defaultValue
     * @param mandatory TODO
     * @param mdp TODO
     * @param paramNo TODO
     */
    public void buildRDsNameInput(String label, Field field, 
            boolean mandatory, MessageDisplay mdp);
    
    /**
     * 
     * INSERT DOCUMENTATION HERE!
     *
     * @param label
     * @param defaultValue
     * @param mandatory TODO
     * @param mdp TODO
     * @param paramNo TODO
     */
    public void buildRNumericInput(String label, Field field, 
            boolean mandatory, MessageDisplay mdp);
    
    /**
     * 
     * INSERT DOCUMENTATION HERE!
     *
     * @param label
     * @param defaultValue
     * @param mandatory TODO
     * @param mdp TODO
     * @param paramNo TODO
     */
    public void buildRStringInput(String label, Field field, 
            boolean mandatory, MessageDisplay mdp);
    
    /**
     * 
     * INSERT DOCUMENTATION HERE!
     *
     * @return
     */
    public Component getRInterfacePanel();

}