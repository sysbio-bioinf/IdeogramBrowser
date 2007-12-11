/*
 * File:	RLibWrapperReaderTest.java
 * Created: 10.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import static org.junit.Assert.*;
import ideogram.r.rlibwrappers.GLADWrapper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RLibWrapperReaderTest {

    private static RLibWrapperReader rd;
    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        rd = new RLibWrapperReader();
    }

    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link ideogram.r.gui.RLibWrapperReader#createInputPanel(java.lang.String)}.
     * @throws ClassNotFoundException 
     * @throws RLibraryWrapperException 
     */
    @Test
    public void testCreateInputPanelString() 
    throws ClassNotFoundException, IllegalArgumentException{
        rd.createInputPanel(new GLADWrapper());
    }

    @Test
    public void testCreateInputPanel() 
    throws IllegalArgumentException, ClassNotFoundException {
        rd.createInputPanel();
    }
}
