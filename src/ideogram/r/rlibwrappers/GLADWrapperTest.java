/*
 * File:	GLADWrapperTest.java
 * Created: 09.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import static org.junit.Assert.*;

import ideogram.r.RController;
import ideogram.r.RDataSetWrapper;
import ideogram.r.exceptions.RException;
import ideogram.r.gui.RGuiWindow;

import java.util.List;

import javax.swing.WindowConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rosuda.JRI.RBool;
import org.rosuda.JRI.REXP;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class GLADWrapperTest {
    
    private static GLADWrapper gw;

    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RController.checkVersion();
        RController.getInstance().startEngine();
        RGuiWindow w = new RGuiWindow();
        w.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gw = new GLADWrapper();
    }

    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Thread.sleep(20000);
    }

    /**
     * Test method for {@link ideogram.r.rlibwrappers.GLADWrapper#hasSampleData()}.
     */
    @Test
    public void testHasSampleData() {
        assertTrue(gw.hasSampleData());
    }

    /**
     * Test method for {@link ideogram.r.rlibwrappers.GLADWrapper#listSampleData()}.
     */
    @Test
    public void testListSampleData() {
        List<RDataSetWrapper> l = gw.listSampleData();
        boolean b = (l != null);
        if (b) {
            for (RDataSetWrapper dw: l) {
                System.out.println(dw.toString());
            }
        }
        assertTrue(b);
    }

    /**
     * Test method for {@link ideogram.r.rlibwrappers.GLADWrapper#loadLibrary()}.
     * @throws RException 
     */
    @Test
    public void testLoadLibrary() throws RException {
        gw.loadLibrary();
        assertTrue(true);
    }

    /**
     * Test method for {@link ideogram.r.rlibwrappers.GLADWrapper#loadSampleData(ideogram.r.RDataSetWrapper)}.
     * @throws RException 
     */
    @Test
    public void testLoadSampleData() throws RException {
        gw.loadSampleData(new RDataSetWrapper("snijders"));
        assertTrue(true);
    }
    
    /**
     * Test method for {@link ideogram.r.rlibwrappers.GLADWrapper#useGladFunction(java.lang.String, org.rosuda.JRI.RBool, java.lang.String, double, double, java.lang.String, java.lang.String, double, org.rosuda.JRI.RBool, double, double, double, java.lang.String, double, double, double, java.lang.String, double, org.rosuda.JRI.RBool)}.
     * @throws RException 
     */
    @Test
    public void testUseGladFunction() throws RException {
        REXP res = gw.useGladFunction();
        assertTrue(res != null);
    }

    /**
     * Test method for {@link ideogram.r.rlibwrappers.GLADWrapper#getResult()}.
     * @throws RException 
     */
    @Test
    public void testGetResult() throws RException {
        REXP res = gw.getResult();
        assertTrue(res != null);
    }
//    
//    /**
//     * Test method for {@link ideogram.r.GLADWrapper#unloadLibrary()}.
//     * @throws RException 
//     */
//    @Test
//    public void testUnloadLibrary() throws RException {
//        gw.unloadLibrary();
//        assertTrue(true);
//    }
}
