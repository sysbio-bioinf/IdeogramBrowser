/*
 * File:	AvailableRLibrarys.java
 * Created: 23.02.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.RController;
import ideogram.r.gui.RGuiWindow;

/**
 * Central registry for R libraries that shall be displayed in {@link RGuiWindow}'s
 * "Select Library" dialog. Add new libraries by adding new elements to this enum.
 *  
 * @author Ferdinand Hofherr
 *
 */
public enum RLibraryRegistry {
    GLAD("GLAD", "ideogram.r.rlibwrappers.GLADWrapper");
    
    private final String libName;
    private final String fullyQualifiedName;
    
    private RLibraryRegistry(String libName, String fullyQualifiedName) {
        this.libName = libName;
        this.fullyQualifiedName = fullyQualifiedName;
    }
    
    public void register() {
        RController.getInstance().registerRLibraryWrapper(libName, 
                fullyQualifiedName);
    }
}
