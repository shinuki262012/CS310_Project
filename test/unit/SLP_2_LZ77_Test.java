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
    // TODO: DELETE
    public String testDir = "C:\\Users\\Tian\\Documents\\GitHub\\CS310_Project\\test\\";
    // TODO: Change it to the test folder
    // public String testDir = "";
    public SLP_2_LZ77 slp2lz77;
    public Map<String, Pair<String, String>> grammar;

    @Test
    public void test1() {

        slp2lz77 = new SLP_2_LZ77();
        grammar = new HashMap<String, Pair<String, String>>();
        slp2lz77.SLP2LZ77(testDir + "tt.cfg");
        assertEquals(new SLP_2_Text().toText(new ParseCFG(testDir + "tt.cfg").getCFG()), slp2lz77.decompressLZ77());
    }
}
