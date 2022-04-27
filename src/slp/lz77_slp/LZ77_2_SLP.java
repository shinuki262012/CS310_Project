package slp.lz77_slp;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.Map;
import java.util.HashMap;

import slp.util.*;
import slp.text_slp.SLP_2_Text;
import slp.text_slp.Text_2_SLP;

/**
 * Parse the input .gz file to obtain a LZ77 parsing of size z of text T.
 * Then compute the SLP of the LZ77.
 * 
 * @author Tianlong Zhong
 */
public class LZ77_2_SLP {
    String text; // text encoded by the .gz file.
    slp.lz77_slp.gzip_parser.GzipDecompress gzipParser;
    ArrayList<Integer> lz77Parsing;
    ArrayList<Pair<Integer, Integer>> factorization;
    Map<String, Pair<String, String>> grammar;
    public static long nonTerminalCounter = 0;

    public LZ77_2_SLP() {
        text = "";
        gzipParser = new slp.lz77_slp.gzip_parser.GzipDecompress();
        lz77Parsing = new ArrayList<Integer>();
        factorization = new ArrayList<Pair<Integer, Integer>>();
        grammar = new HashMap<String, Pair<String, String>>();
    }

    /**
     * Parse the input file to obtian the LZ77 factorization
     * 
     * @param file input .gz file
     */
    public void parseGzip(String file) {
        /* Parse input .gz file into LZ77 parsing */
        ArrayList<Integer> lz77Parsing = new ArrayList<>();
        try {
            lz77Parsing = gzipParser.parseGzip2LZ77(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lz77Parsing == null || lz77Parsing.size() % 2 != 0) {
            System.out.println("Failed to parse the file");
            System.exit(1);
        }

        for (int i = 0; i < lz77Parsing.size(); i += 2) {
            factorization.add(new Pair<Integer, Integer>(lz77Parsing.get(i), lz77Parsing.get(i + 1)));
        }

        /* Convert backreferencing LZ77 into position referencing LZ77 */
        int position = 0;
        for (Pair<Integer, Integer> p : factorization) {
            if (p.second.equals(0)) {
                position += 1;
            } else {
                p.first = (position - (int) p.first);
                position += (int) p.second;
            }
        }

        /* Convert LZ77 parsing into SLP */
        try {
            FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gzipis = new GZIPInputStream(fis);
            InputStreamReader isr = new InputStreamReader(gzipis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                text += line;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Unable to decompress the input file");
        }
        Text_2_SLP t = new Text_2_SLP();
        grammar = t.compress(text, factorization);

        /* Save to file */
        file = file.substring(0, file.length() - 3);
        try {
            PrintWriter outputWriter = new PrintWriter(file + ".cfg");
            for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
                outputWriter.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
                // rule.getValue().second);
            }
            System.out.println("Output successfully saved to " + file + ".cfg \n");
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }

    }

    /**
     * Test method
     * 
     * @return true if the result of decompressing the SLP matches that of
     *         decompressing .gz file.
     */
    public boolean testResult() {
        return this.text.equals(new SLP_2_Text().toText(grammar));
    }

}