package unit;

import slp.lz77_slp.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* .gz file -> .cfg file, test by comparing the results of coverting them to text */
public class LZ77_2_SLP_Test {

    // TODO: Change it to suit your test folder
    public String testDir = "~\\test\\";
    public LZ77_2_SLP lz772slp;

    @Test
    public void test1() {
        lz772slp = new LZ77_2_SLP();
        lz772slp.parseGzip(testDir + "t.gz");
        assertEquals(true, lz772slp.testResult());
    }

    @Test
    public void test2() {
        lz772slp = new LZ77_2_SLP();
        lz772slp.parseGzip(testDir + "128random.gz");
        assertEquals(true, lz772slp.testResult());
    }

    @Test
    public void test3() {
        lz772slp = new LZ77_2_SLP();
        lz772slp.parseGzip(testDir + "repeated.gz");
        assertEquals(true, lz772slp.testResult());
    }

}
