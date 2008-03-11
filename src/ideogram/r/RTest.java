/*
 * File:	RTest.java
 * 
 * Created: 	01.12.2007
 * 
 * Author: 	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.exceptions.RException;
import ideogram.r.gui.RGuiWindow;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.rosuda.JRI.RBool;

/**
 * INSERT DOCUMENTATION HERE!
 * 
 * @author Ferdinand Hofherr
 */
public class RTest {

    /**
     * INSERT DOCUMENTATION HERE!
     * 
     * @param args
     */
    public static void main(String[] args) {
	JFrame rGui;
	try {
	    RController.checkVersion();
	    rGui = new RGuiWindow();
	    rGui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    RController.getInstance().startEngine();
	    RBool t = new RBool(1);
	    RBool f = new RBool(0);
	    RBool n = new RBool(2);
	    System.out.println("true: " + t.isTRUE() + "\nfalse: "
		    + f.isFALSE() + "\nNA: " + n.isNA());
	} catch (RException e) {
	    e.printStackTrace();
	}
    }

}
