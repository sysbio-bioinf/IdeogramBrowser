/*
 * File:	RDataSetWrapper.java 
 * 
 * Created: 	08.12.2007
 *  
 * Author: 	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for data sets associated with a certain package.
 * 
 * @author Ferdinand Hofherr
 */
public class RDataSetWrapper {

    private String name;
    private String description;

    /*
     * List of available subdatasets. GLAD's dataset 'veltman' for example
     * contains many of those.
     */
    private List<String> elements;

    public RDataSetWrapper(String name) {
	this.name = name;
	elements = new ArrayList<String>();
    }

    public RDataSetWrapper(String name, String description) {
	this(name);
	this.description = description;
    }

    /**
     * Get the data set's name.
     * 
     * @return The data set's name.
     */
    public String getName() {
	return name;
    }

    /**
     * Get the data set's description.
     * 
     * @return The data set's description.
     */
    public String getDescription() {
	return description;
    }

    /**
     * Add an elements (i.e. a sub data set) to the wrapper.
     * 
     * @param elmName
     */
    public void addElement(String elmName) {
	elements.add(elmName);
    }

    /**
     * Get a list of all elements belonging to this data set.
     * 
     * @return List of element names.
     */
    public List<String> getElements() {
	return elements;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return name + " - " + description;
    }
}
