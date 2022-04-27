package unit;

import java.util.Map;
import java.util.HashMap;

import slp.util.*;
import slp.lz77_slp.*;
import slp.text_slp.SLP_2_Text;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* .gz file -> .cfg file, test by comparing the results of coverting them to text */
public class SLP_2_LZ77_Test {
    // TODO: Change it to suit your test folder
    public String testDir = "~\\test\\";
    public SLP_2_LZ77 slp2lz77;
    public Map<String, Pair<String, String>> grammar;

    @Test
    public void test1() {
        slp2lz77 = new SLP_2_LZ77();
        grammar = new HashMap<String, Pair<String, String>>();
        slp2lz77.slp_2_lz77(testDir + "t.cfg");
        assertEquals(new SLP_2_Text().toText(new ParseCFG(testDir + "t.cfg").getCFG()), slp2lz77.decompressLZ77());
    }

    @Test
    public void test2() {
        slp2lz77 = new SLP_2_LZ77();
        grammar = new HashMap<String, Pair<String, String>>();
        slp2lz77.slp_2_lz77(testDir + "128random.cfg");
        assertEquals(new SLP_2_Text().toText(new ParseCFG(testDir + "128random.cfg").getCFG()),
                slp2lz77.decompressLZ77());
    }

    @Test
    public void test3() {
        slp2lz77 = new SLP_2_LZ77();
        grammar = new HashMap<String, Pair<String, String>>();
        slp2lz77.slp_2_lz77(testDir + "repeated.cfg");
        assertEquals(new SLP_2_Text().toText(new ParseCFG(testDir + "repeated.cfg").getCFG()),
                slp2lz77.decompressLZ77());
    }
}
