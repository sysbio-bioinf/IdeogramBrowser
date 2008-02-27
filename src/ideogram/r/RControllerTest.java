/*
 * File: RControllerTest.java Created: 29.11.2007 Author: Ferdinand Hofherr
 * <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import static org.junit.Assert.*;
import ideogram.r.exceptions.JRIVersionException;
import ideogram.r.exceptions.RException;
import ideogram.r.gui.RGuiWindow;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rosuda.JRI.Rengine;

/**
 * INSERT DOCUMENTATION HERE!
 * 
 * @author Ferdinand Hofherr
 */
public class RControllerTest {

    private static JFrame gui;

    /**
     * INSERT DOCUMENTATION HERE!
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RController.checkVersion();
        gui = new RGuiWindow();
    }

    // /**
    // * INSERT DOCUMENTATION HERE!
    // *
    // * @throws java.lang.Exception
    // */
    // @AfterClass
    // public static void tearDownAfterClass() throws Exception {
    // }

    // @Before
    // public void setUp() throws Exception {
    // System.out.println("VERSION CHECK");
    // RController.checkVersion();
    // // Start a brand new R process
    // System.out.println("STARTING R");
    // RController.getInstance().getEngine();
    // }
    //
    // /**
    // * @throws java.lang.Exception
    // */
    // @After
    // public void tearDown() throws Exception {
    // System.in.read();
    // System.out.println("\n\nSTOPPING R\n\n\n");
    // RController.getInstance().stopEngine();
    // }

    /**
     * Test method for {@link ideogram.r.RController#checkVersion()}.
     */
    @Test
    public void testCheckVersion() throws JRIVersionException {
        RController.checkVersion();
        assertTrue(true);
    }

    /**
     * Test method for {@link ideogram.r.RController#getInstance()}.
     */
    @Test
    public void testGetInstance() {
        assertTrue(RController.getInstance() != null);
    }

    @Test
    public void startEngine() throws RException {
        RController.getInstance().startEngine();
    }

    /**
     * Test method for {@link ideogram.r.RController#getEngine()}.
     */
    @Test
    public void testGetEngine() throws RException {
        assertTrue(RController.getInstance().getEngine() != null);
    }

    /**
     * Test method for
     * {@link ideogram.r.RController#loadRLibrary(java.lang.String)}.
     * 
     * @throws RException
     */
    @Test
    public void testLoadRLibrary() throws RException {
        RController.getInstance().loadRLibrary("splines");
        assertTrue(true);
    }

    /**
     * Test method for {@link ideogram.r.RController#stopEngine()}.
     * 
     * @throws RException
     */
    @Test
    public void testStopEngine() throws RException {
        RController.getInstance().getEngine(); // assure the engine is
        // running!
        assertTrue(RController.getInstance().stopEngine());
    }
}
