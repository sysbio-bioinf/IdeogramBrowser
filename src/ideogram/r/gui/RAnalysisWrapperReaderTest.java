/*
 * File:	RLibraryWrapperReaderTest.java
 * 
 * Created: 	10.12.2007
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.exceptions.RException;
import ideogram.r.exceptions.RLibraryWrapperException;
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
public class RAnalysisWrapperReaderTest {

    private static RAnalysisWrapperReader rd;

    /**
     * INSERT DOCUMENTATION HERE!
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	rd = new RAnalysisWrapperReader();
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
     * Test method for
     * {@link ideogram.r.gui.RAnalysisWrapperReader#createInputPanel(java.lang.String)}.
     * 
     * @throws ClassNotFoundException
     * @throws RException
     * @throws RLibraryWrapperException
     */
    @Test
    public void testCreateInputPanelString() throws ClassNotFoundException,
	    IllegalArgumentException, RException {
	rd.createInputPanel(new GLADWrapper());
    }

    @Test
    public void testCreateInputPanel() throws IllegalArgumentException,
	    ClassNotFoundException {
	rd.createInputPanel();
    }
}
