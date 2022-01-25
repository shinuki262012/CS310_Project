package SLP.LZ77_SLP;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import SLP.LZ77_SLP.gzip_parser.*;

public class LZ77_2_SLP {

    SLP.LZ77_SLP.gzip_parser.GzipDecompress gzipParser = new SLP.LZ77_SLP.gzip_parser.GzipDecompress();

    public void parseGzip() {
        try {
            String[] files = new String[2];
            files[0] = "..\\test\\t.gz";
            files[1] = "..\\test\\t";
            ArrayList<Integer> LZ77Parsing = gzipParser.parseGzip2LZ77(files[0]);
            System.out.println(LZ77Parsing.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LZ77_2_SLP().parseGzip();
    }
}