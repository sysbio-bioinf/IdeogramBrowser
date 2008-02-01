package ideogram.input;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.FileFormatException;

public class RResultReaderModelTest {
    private RResultReaderModel rmod;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        rmod = new RResultReaderModel();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadFromFile() throws IOException, FileFormatException {
        File f = new File("test_data/RResultReaderTestData.RResult");
        rmod.loadFromFile(f);
        assertTrue(true);
    }

}
