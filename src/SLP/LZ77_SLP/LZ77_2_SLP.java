package SLP.LZ77_SLP;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import SLP.LZ77_SLP.gzip_parser.*;

// 
// Parse the input .gz file to obtain a LZ77 parsing of size z of text T, then compute the AVL grammar of size O(z log n)
//

public class LZ77_2_SLP {

    SLP.LZ77_SLP.gzip_parser.GzipDecompress gzipParser = new SLP.LZ77_SLP.gzip_parser.GzipDecompress();
    public static long nonTerminalCounter = 0;

    public void parseGzip() {
        ArrayList<Integer> LZ77Parsing = new ArrayList<>();
        try {
            String[] files = new String[2];
            files[0] = "..\\test\\t.gz";
            files[1] = "..\\test\\t";
            LZ77Parsing = gzipParser.parseGzip2LZ77(files[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LZ77Parsing == null) {
            System.exit(1);
        }
        // Declare types
        // Initialize the parsing reader
        // Compute the parsing size
        // Init Karp-Rabin hasing
        // Compute the AVL grammar expanding to T
        // Clean up
        // Return the grammar
    }

    public static void main(String[] args) {
        new LZ77_2_SLP().parseGzip();
    }

}