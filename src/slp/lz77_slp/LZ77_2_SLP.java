package slp.lz77_slp;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

import slp.Main;
import slp.util.*;
import slp.lz77_slp.gzip_parser.*;
import slp.text_slp.SLP_2_Text;
import slp.text_slp.Text_2_SLP;

// 
// Parse the input .gz file to obtain a LZ77 parsing of size z of text T, then compute the AVL grammar of size O(z log n)
//

public class LZ77_2_SLP {
    String text;
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

        for (Pair p : factorization) {
            System.out.println(p.first + "," + p.second);
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
        System.out.println("input string: " + text);
        Text_2_SLP t = new Text_2_SLP();
        grammar = t.compress(text, factorization);

        /* Save to file */
        file = file.substring(0, file.length() - 3);
        try {
            PrintWriter outputWriter = new PrintWriter(file);
            for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
                outputWriter.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
                System.out.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
            }
            System.out.println("Output successfully saved to " + file + ".cfg \n");
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }

    }

    public boolean testResult() {
        return this.text.equals(new SLP_2_Text().toText(grammar));
    }

}