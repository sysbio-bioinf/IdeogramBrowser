package ideogram.r.gui.inputwidgets;

import ideogram.r.exceptions.InvalidInputException;
import ideogram.r.gui.MessageDisplay;
import ideogram.r.rlibwrappers.RLibraryWrapper;

public interface RInputWidget {

    /**
     * Returns true, if input to this field is mandatory.
     *
     * @return
     */
    public abstract boolean isMandatory();

    /**
     * Reference to a class, which is able to display status messages. Set
     * to null, if no such class exists.
     *
     * @param mdp
     */
    public abstract void setMessageDisplay(MessageDisplay mdp);

    /**
     * Get a reference to an object, which implements the {@link MessageDisplay}
     * interface. If no such class exists, null will be returned.
     *
     * @return Reference to an object whose class implements 
     *  {@link MessageDisplay}, or null.
     */
    public abstract MessageDisplay getMessageDisplay();

    /**
     * Assure the user input is valid. Throw an exception if it ain't.
     *
     * @return true if input is valid, else false.
     * @throws InvalidInputException
     */
    public abstract boolean validateInput() throws InvalidInputException;

    /**
     * Reset the input field to its default value. This also assures, that 
     * the corresponding input field of the underlying {@link RLibraryWrapper}
     * will be set to its default value. Note, that this does not have to happen
     * immediately! Although it is assured, that the fields will be set to 
     * correct values, before any analysis is performed. 
     */
    public abstract void resetToDefault();

    /**
     * Returns true, when the field is not set to a value.
     */
    public abstract boolean isEmpty();

}