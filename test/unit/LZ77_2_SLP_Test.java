package unit;

import slp.util.*;
import slp.lz77_slp.*;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/* .gz file -> .cfg file, test by comparing the results of coverting them to text */
public class LZ77_2_SLP_Test {
    // TODO: DELETE
    public String testDir = "C:\\Users\\Tian\\Documents\\GitHub\\CS310_Project\\test\\";
    // TODO: Change it to the test folder
    // public String testDir = "";
    public LZ77_2_SLP lz772slp;

    @Test
    public void test1() {
        lz772slp = new LZ77_2_SLP();
        lz772slp.parseGzip(testDir + "t.gz");
        // assertEquals(true, lz772slp.testResult());
        assertEquals(false, lz772slp.testResult());
    }
}
