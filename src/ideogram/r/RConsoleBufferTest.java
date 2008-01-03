/*
 * File:	StringRingBufferTest.java
 * Created: 02.01.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import static org.junit.Assert.*;

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
public class RConsoleBufferTest {

    private RConsoleBuffer rb;
    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
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
        rb = new RConsoleBuffer(10);
    }

    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        rb = null;
    }

    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#flush()}.
     */
    @Test
    public void testFlush() {
        System.out.println("testFlush()");
        rb.insert("Hello");
        rb.flush();
        assertTrue(rb.isEmpty());
    }

    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        /* Each test starts with a new empty buffer. If this fails something is
         * seriously wrong. */
        assertTrue(rb.isEmpty());
    }

    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#insert(java.lang.String)}.
     */
    @Test
    public void testInsert1() {
        System.out.println("\ntestInsert1()");
        rb.insert("Hello"); // smaller than buffer size
        System.out.println(rb.toString());
        assertTrue(rb.toString().equals("Hello"));
    }
    
    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#insert(java.lang.String)}.
     */
    @Test
    public void testInsert2() {
        System.out.println("\ntestInsert2()");
        rb.insert("HelloHello"); // exactly buffer size
        System.out.println(rb.toString());
        assertTrue(rb.toString().equals("HelloHello"));
    }
    
    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#insert(java.lang.String)}.
     */
    @Test
    public void testInsert3() {
        System.out.println("\ntestInsert3()");
        rb.insert("12345HelloWorld"); // bigger than buffer size, 12345
                                      // should be overwritten by World.
        System.out.println(rb.toString());
        assertTrue(rb.toString().equals("HelloWorld"));
    }
    
    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#insert(java.lang.String)}.
     */
    @Test
    public void testInsert4() {
        System.out.println("\ntestInsert4()");
        rb.insert("1234567890"); //exactly buffer size
        System.out.println(rb.toString());
        rb.insert("HelloWorld");
        System.out.println(rb.toString());
        assertTrue(rb.toString().equals("HelloWorld"));
    }

    /**
     * Test method for {@link ideogram.r.RConsoleBuffer#toString()}.
     */
    @Test
    public void testToString() {
        System.out.println("\ntestToString()");
        rb.insert("HelloWorld"); // exactly buffer size
        System.out.println(rb.toString());
        rb.toString().equals("HelloWorld");
    }

}
