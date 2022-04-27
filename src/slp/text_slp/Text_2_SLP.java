package slp.text_slp;

import slp.Main;
import slp.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/**
 * Grammar based compression implementation based on
 * Artur Jeż. "A Really Simple Approximation of Smallest Grammar".
 * In: Theoretical Computer Science 616 (Mar. 2014).
 * doi: 10.1007/978-3-319-07566-2_19.
 * 
 * @author Tianlong Zhong
 */
public class Text_2_SLP {
    public static long nonTerminalCounter; // nonterminal counter
    public static Map<String, Pair<String, String>> grammar; // SLP
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    /**
     * Cosntructor
     */
    public Text_2_SLP() {
        nonTerminalCounter = 0;
        grammar = new HashMap<String, Pair<String, String>>();
    }

    /**
     * Main function for compressing
     */
    public void text_2_slp() {
        System.out.println("Choose the file to compress: ");
        String file = Main.inputScanner.nextLine();
        // Check if file exists
        if (!new File(file).isFile()) {
            System.out.println("Error: input is not a file: " + file);
            return;
        }

        // Read input file into a String
        String input = "";
        try {
            input = new String(Files.readString(Paths.get(file)));
        } catch (IOException e) {
            System.out.println("Error: Failed to read fil: " + file);
        }

        // Compress the input, output to file
        try {
            PrintWriter outputWriter = new PrintWriter(file + ".cfg");
            // Compress the input
            compress(input);
            // Output to a file
            for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
                outputWriter.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
            }
            System.out.println("Output successfully saved to " + file + ".cfg \n");
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }
    }

    /**
     * Method used for given input file
     * 
     * @param file input file
     */
    public void text_2_slp(String file) {
        // Check if file exists
        if (!new File(file).isFile()) {
            System.out.println("Error: input is not a file: " + file);
            return;
        }

        // Read input file into a String
        String input = "";
        try {
            input = new String(Files.readString(Paths.get(file)));
        } catch (IOException e) {
            System.out.println("Error: Failed to read fil: " + file);
        }

        // Compress the input, output to file
        try {
            PrintWriter outputWriter = new PrintWriter(file + ".cfg");
            // Compress the input
            compress(input);

            // Output to a file
            for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
                outputWriter.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
            }
            System.out.println("Output successfully saved to " + file + ".cfg \n");
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }
    }

    /**
     * Compress input string into SLP
     * 
     * @param input input string
     * @return SLP that generates the input string
     */
    public Map<String, Pair<String, String>> compress(String input) {

        /* --- Compute the LZ77 factorization of the input --- */
        int w = input.length();
        ArrayList<Pair<Integer, Integer>> factorization = new Factorization().factorization(input);

        // Initialise tables start, end and pair
        int[] start = new int[w]; // stores beginning of factors, -1: not the beginning of a factor
        int[] end = new int[w]; // stores ends of factors: 1/-1 -> whether w[i] is the last letter of a factor
        int[] pair = new int[w]; // stores pairing, 0: unpaired; 1: first in a pair; 2: second in a pair
        Arrays.fill(start, -1);
        Arrays.fill(end, -1);
        Arrays.fill(pair, 0);

        // Populate tables with the factorization
        int curIdx = 0;
        for (int i = 0; i < factorization.size(); i++) {
            int first = factorization.get(i).first;
            int second = factorization.get(i).second;

            if (second == 0) { // a free letter
                start[curIdx] = curIdx;
                end[curIdx] = 1;
                curIdx += 1;
            } else {
                start[curIdx] = first;
                curIdx += second;
                end[curIdx - 1] = 1;
            }
        }

        String[] input_array = input.split("");

        /* --- Main loop --- */
        while (input_array.length > 1) {
            pairing(input_array, start, end, pair); // devise pairings of w
            input_array = pairReplacement(input_array, start, end, pair); // replace the pairs
        }

        // Change the start symbol to S00
        String start_symbol = long_2_nonterminal(nonTerminalCounter - 1);
        grammar.put("S00", grammar.get(start_symbol));
        grammar.remove(start_symbol);

        return grammar;
    }

    /**
     * Compress the given input into SLP using given LZ77 factorization of the input
     * 
     * @param input         input string
     * @param factorization the LZ77 factorization of the input string
     * @return SLP that generated the input string
     */
    public Map<String, Pair<String, String>> compress(String input, ArrayList<Pair<Integer, Integer>> factorization) {
        int w = input.length();
        // Initialise tables start, end and pair
        int[] start = new int[w]; // stores beginning of factors, -1: not the beginning of a factor
        int[] end = new int[w]; // stores ends of factors: 1/-1 -> whether w[i] is the last letter of a factor
        int[] pair = new int[w]; // stores pairing, 0: unpaired; 1: first in a pair; 2: second in a pair
        Arrays.fill(start, -1);
        Arrays.fill(end, -1);
        Arrays.fill(pair, 0);

        // Populate tables with the factorization
        int curIdx = 0;
        for (int i = 0; i < factorization.size(); i++) {
            int first = factorization.get(i).first;
            int second = factorization.get(i).second;

            if (second == 0) { // a free letter
                start[curIdx] = curIdx;
                end[curIdx] = 1;
                curIdx += 1;
            } else { // a factor
                start[curIdx] = first;
                curIdx += second;
                end[curIdx - 1] = 1;
            }
        }

        String[] input_array = input.split("");

        // Main loop
        while (input_array.length > 1) {
            // Compute a pairing of w using pairing()
            pairing(input_array, start, end, pair);

            // Replace the pairs using pairReplacement()
            input_array = pairReplacement(input_array, start, end, pair);
        }

        // Change the start symbol to S00
        String start_symbol = long_2_nonterminal(nonTerminalCounter - 1);
        grammar.put("S00", grammar.get(start_symbol));
        grammar.remove(start_symbol);

        return grammar;
    }

    /**
     * Devise pairings
     * 
     * @param input array of input string
     * @param start beginning of factors
     * @param end   end of factors
     * @param pair  pairing info
     */
    private void pairing(String[] input, int[] start, int[] end, int[] pair) {
        int w = input.length;

        // Check if the first letter is a free letter
        if (start[0] != -1) {
            if (end[0] == 1) {
                start[0] = end[0] = -1;
            }
        }
        int i = 1;
        int j = 0;
        pair[0] = 0;
        while (i < w) {
            if (start[i] != -1) { // w[i] is the first element of a factor
                if (end[i] == 1) { // This is a one-letter factor
                    start[i] = end[i] = -1; // Turn this letter into a free letter
                } else if (start[i] == i - 1) { // the factor is a^k, its definition begins one position to the left
                    start[i + 1] = i - 1; // Move the definition of the factor
                    start[i] = -1; // make w[i] a free letter
                } else if (pair[start[i]] != 1) { // The pairing of the definition of factor is bad:
                    start[i + 1] = start[i] + 1; // shorten the factor definition by 1 on the left
                    start[i] = -1; // make a free letter
                } else { // Good factor
                    j = start[i]; // factor's definition starts at position j
                    do { // copy the pairing from the factor definition
                        pair[i] = pair[j];
                        i++;
                        j++;
                    } while (end[i - 1] != 1);
                    while (pair[i - 1] != 2) { // looking for a new end of the factor
                        i--;
                        end[i - 1] = 1; // shorten the factor by 1 on the right
                        end[i] = -1;
                        pair[i] = 0; // clear the pairing
                    }
                }
            }
            if (i < w) {
                if (start[i] == -1) { // w[i] is a free letter
                    if (pair[i - 1] == 0) { // if previous letter is not paired
                        pair[i - 1] = 1; // pair them
                        pair[i] = 2;
                    } else {
                        pair[i] = 0; // leave the letter unpaired
                    }
                    i++;
                }
            }
        }

        return;
    }

    /**
     * Replace devised pairings with nonterminals
     * 
     * @param input array of input string
     * @param start beginning of factors
     * @param end   end of factors
     * @param pair  pairing info
     * @return updated array of input string
     */
    private String[] pairReplacement(String[] input, int[] start, int[] end, int[] pair) {
        String[] inputP = new String[input.length]; // the new word after replacing the pairing
        int[] newpos = new int[input.length];
        Arrays.fill(newpos, -1);

        int i = 0;
        int iP = 0; // iP is the position corresponding to i in the new word
        int jP = 0;
        while (i < input.length) {

            if (start[i] != -1) { // w[i] is the first element of a factor
                start[iP] = newpos[start[i]];
                jP = newpos[start[i]]; // factor in the new word begins at the position corresponding to the
                // beginning of the current factor
                start[i] = -1;
                do {
                    newpos[i] = iP; // position corresponding to i
                    inputP[iP] = inputP[jP]; // copy the letter according to new factorization
                    iP++;
                    jP++;
                    if (pair[i] == 1) {
                        i += 2; // move left by the whole pair
                    } else {
                        i++; // move left by the unpaired letter
                    }
                } while (end[i - 1] == -1);
                end[iP - 1] = 1; // End in the new word
                end[i - 1] = -1; // Clearing obsolete info
            }
            if (i < input.length) {
                if (start[i] == -1) { // w[i] is a free letter
                    newpos[i] = iP;
                    if (pair[i] == 0) {
                        inputP[iP] = input[i]; // copy the unpaired letter
                        i++; // move by this letter to the right
                        iP++;
                    } else {
                        String nonTerminal = long_2_nonterminal(nonTerminalCounter);
                        nonTerminalCounter++;
                        Pair<String, String> rhs = new Pair<String, String>(input[i], input[i + 1]);
                        inputP[iP] = nonTerminal; // Paired free letters are replaced by a fresh letter

                        // Record the grammar ruls
                        grammar.put(nonTerminal, rhs);
                        i += 2;
                        iP += 1;
                    }
                }
            }
        }
        return Arrays.copyOfRange(inputP, 0, iP);
    }

    /**
     * Get the nonterminal represented by the given integer
     * 
     * @param x an integer
     * @return the nontermial representing the integer
     */
    private String long_2_nonterminal(long x) {
        int n = (int) x / 26;
        return alphabets[(int) x % 26] + String.valueOf(n);
    }

    /**
     * Display the menu
     * 
     * @param args
     */
    public void main(String[] args) {
        String file;
        while (true) {
            // Display options
            System.out.println("1 - text to cfg");
            System.out.println("2 - cfg to grammar tree");
            System.out.println("3 - cfg to text");
            System.out.println("q - Exit");
            String inputs = Main.inputScanner.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given\n");
                break;
            }
            if (inputs.charAt(0) == 'q') {
                System.out.println("Exiting...\n");
                break;
            }
            switch (inputs.charAt(0)) {
                case '1': {
                    Text_2_SLP t = new Text_2_SLP();
                    t.text_2_slp();
                    break;
                }
                case '2': {
                    // cfg -> tree
                    System.out.println("Choose the file to visualise: ");
                    file = Main.inputScanner.nextLine();
                    // Check if file exists
                    if (!new File(file).isFile()) {
                        System.out.println("Error: File does not exist.");
                        break;
                    }
                    // Check file format
                    if (!file.substring(file.length() - 4, file.length()).equals(".cfg")) {
                        System.out.println("Wrong file type, please select a file of type .cfg");
                    } else {
                        // To tree
                        SLP_2_Text s = new SLP_2_Text();
                        s.toTree(new ParseCFG(file).getCFG());
                        break;
                    }
                }
                case '3': {
                    // Get input file
                    System.out.println("Choose the file to decompress: ");
                    file = slp.Main.inputScanner.nextLine();

                    // Check if file exists
                    if (!new File(file).isFile()) {
                        System.out.println("Error: File does not exist.");
                        break;
                    }
                    // Check file format
                    if (!file.substring(file.length() - 4, file.length()).equals(".cfg")) {
                        System.out.println("Wrong file type, please select a file of type .cfg");
                    } else {
                        // cfg -> text
                        SLP_2_Text s = new SLP_2_Text();
                        s.toText(new ParseCFG(file).getCFG(), file.substring(0, file.length() - 4));
                    }

                    break;
                }
                default:
                    System.out.println("Please enter a valid option. \n");
                    break;
            }
        }
    }

}