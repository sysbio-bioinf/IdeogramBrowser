/*
 * File:	RDataSetWrapper.java
 * Created: 08.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import java.util.ArrayList;
import java.util.List;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RDataSetWrapper {

    private String name;
    private String description;
    private List<String> elements;
    
    public RDataSetWrapper(String name) {
        this.name = name;
        elements = new ArrayList<String>();
    }
    
    public RDataSetWrapper(String name, String description) {
        this(name);
        this.description = description;
    }

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void addElement(String elmName) {
        elements.add(elmName);
    }
    
    public List<String> getElements() {
        return elements;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name + " - " + description;
    }
}
