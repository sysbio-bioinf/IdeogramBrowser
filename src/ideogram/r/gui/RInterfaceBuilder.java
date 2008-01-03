package ideogram.r.gui;

import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.JTabbedPane;

public interface RInterfaceBuilder {

    /**
     * Tell the Builder that it shall start to create a new Interface panel.
     *
     * @param model
     */
    public void createNewRInterfacePanel(RLibraryWrapper model);

    /**
     * Create an interface to an new analysis method. All successive calls to
     * build* methods will affect this interface until createAnalysisInterface
     * is called again. One {@link RLibraryWrapper} may provide more than 
     * one analysis function. Each analysis function should then receive an
     * analysis interface of its own.
     *
     * @param methodName
     */
    public void createAnalysisInterface(String methodName);
    
    /**
     * Build an input field for R boolean values.
     *
     * @param label
     * @param mandatory TODO
     * @param defaultValue
     * @param paramNo TODO
     */
    public void buildRBoolInput(String label, Field field, 
            boolean mandatory);
    
    /**
     * Build an input field for data set names.
     *
     * @param label
     * @param defaultValue
     * @param mandatory TODO
     * @param paramNo TODO
     */
    public void buildRDsNameInput(String label, Field field, boolean mandatory);
    
    /**
     * Build an input field for R numeric values.
     *
     * @param label
     * @param defaultValue
     * @param mandatory TODO
     * @param paramNo TODO
     */
    public void buildRNumericInput(String label, Field field, boolean mandatory);
    
    /**
     * Build an input field for R String values.
     *
     * @param label
     * @param defaultValue
     * @param mandatory TODO
     * @param paramNo TODO
     */
    public void buildRStringInput(String label, Field field, boolean mandatory);
    
    /**
     * Build an button, which allows to perform the selected analysis.
     * 
     * @param funcname Name of the analysis function.
     *
     */
    public void buildPerformButton(String funcname);
    
    /**
     * Build an button, which allows to reset all fields to their default 
     * values.
     *
     */
    public void buildResetButton();
    
    /**
     * Return the built {@link RInterfacePanel}. It will contain all analysis 
     * interfaces. A method to switch between the different analysis functions
     * will be provided.
     *
     * @return
     */
    public Component getRInterfacePanel();
    
    /**
     * Set a reference to an object implementing the {@link MessageDisplay}
     * interface.
     *
     * @param mdp
     */
    public void setMessageDisplay(MessageDisplay mdp);
    
    /**
     * Get the currently set {@link MessageDisplay}.
     *
     * @return
     */
    public MessageDisplay getMessageDisplay();

}