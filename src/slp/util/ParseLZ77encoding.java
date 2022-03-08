package slp.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Parse the input .lz77 file into a lz77 factorization
 * 
 * @author Tianlong Zhong
 */
public class ParseLZ77encoding {
    String path;
    public ArrayList<Pair<Integer, Integer>> lz77;

    public ParseLZ77encoding(String path) {
        this.path = path;
        lz77 = new ArrayList<Pair<Integer, Integer>>();
    }

    public ArrayList<Pair<Integer, Integer>> getLZ77() {
        // Read the file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split at ","
                lz77.add(new Pair<Integer, Integer>(Integer.parseInt(line.split(", ")[0]),
                        Integer.parseInt(line.split(", ")[1])));
            }
        } catch (Exception e) {
            System.out.println("Failed to read the file");
            System.exit(1);
        }
        return lz77;
    }
}
