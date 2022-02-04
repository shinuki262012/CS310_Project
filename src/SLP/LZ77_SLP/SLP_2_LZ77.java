package SLP.LZ77_SLP;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import SLP.Pair;
import SLP.util.parseCFG;

public class SLP_2_LZ77 {

    public static Map<String, Pair<String, String>> cfg = new HashMap<String, Pair<String, String>>();
    
    /**
     * LZ77 factorization without self-references
     * Given factors f1 to f{i-1}, for factor fi, do a binary search on the length
     * of the factor
     * From 1 to the current length
     * Create a new SLP of that length, and conduct pattern matching on the input
     * SLP, which requires compressed pattern matching 
     * if a matching exists, then the length of fi could be longer
     */
    public void SLP2LZ77() {
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("Choose the SLP file to convert: ");
        String file = inputScanner.nextLine();
        try {
            cfg = new parseCFG(file).getCFG();
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputScanner.close();
    }

    public static void main(String[] args) {
        new SLP_2_LZ77().SLP2LZ77();
    }
}
