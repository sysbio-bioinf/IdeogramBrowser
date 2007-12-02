/*
 * File:	RTest.java
 * Created: 01.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
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
        } catch (RException e) {
            e.printStackTrace();
        }
    }

}
