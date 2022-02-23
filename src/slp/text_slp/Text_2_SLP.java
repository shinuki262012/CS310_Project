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
 * Artur Je˙z. "A Really Simple Approximation of Smallest Grammar".
 * In: Theoretical Computer Science 616 (Mar. 2014).
 * doi: 10.1007/978-3-319-07566-2_19.
 * 
 * @author Tian
 */
public class Text_2_SLP {
    public static long nonTerminalCounter;
    public static Map<String, Pair<String, String>> grammar;
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public Text_2_SLP() {
        nonTerminalCounter = 0;
        grammar = new HashMap<String, Pair<String, String>>();
    }

    public void text_2_slp(boolean output_cfg) {
        System.out.println("Choose the file to convert: ");
        String file = Main.inputScanner.nextLine();
        // Check if file exists
        if (!new File(file).isFile()) {
            System.out.println("Error: File does not exist.");
            return;
        }
        // Read input file into a String
        String input = "";
        try {
            input = new String(Files.readString(Paths.get(file)));
        } catch (IOException e) {
            System.out.println("Failed to read file.");
        }

        try {
            PrintWriter outputWriter = new PrintWriter(file + ".cfg");
            // Compress the input
            compress(input);
            // Save as CFG
            if (output_cfg) {
                for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
                    outputWriter.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
                }
                System.out.println("Output successfully saved to " + file + ".cfg \n");
            }
            // Save as a succinct representation
            else {
                CFG_2_POPPT cfg2poppt = new CFG_2_POPPT();
                cfg2poppt.cfg2poppt(grammar, file);
            }
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }
    }

    /**
     * Text to Grammar
     */
    public Map<String, Pair<String, String>> compress(String input) {
        int w = input.length();
        // Compute the LZ77 factorization of the input
        Factorization fac = new Factorization();
        ArrayList<Pair<Integer, Integer>> factorization = Factorization.factorization(input);
        // ArrayList<Pair<Integer, Integer>> factorization = fac.factorization(input);

        // Initialise tables start, end and pair
        int[] start = new int[w]; // stores beginning of factors, -1: not the beginning of a factor
        int[] end = new int[w]; // stores ends of factors: 1/-1 -> whether w[i] is the last letter of a factor
        int[] pair = new int[w]; // stores pairing, 0: unpaired; 1: first in a pair; 2: second in a pair
        Arrays.fill(start, -1);
        Arrays.fill(end, -1);
        Arrays.fill(pair, 0);
        // System.out.println(" ");
        // System.out.println("Start: " + Arrays.toString(start));
        // System.out.println("End: " + Arrays.toString(end));
        // System.out.println("Pair: " + Arrays.toString(pair));
        // System.out.println("------------------------------");

        // Populate tables with the factorization
        int curIdx = 0;
        // System.out.println("LZ77 factorization size: " + factorization.size());
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

        // Main loop
        while (input_array.length > 1) {
            // Compute a pairing of w using pairing()
            pairing(input_array, start, end, pair);

            // Replace the pairs using pairReplacement()
            input_array = pairReplacement(input_array, start, end, pair);
            // System.out.println("pairing: input: " + String.join("", input_array));
        }

        String start_symbol = long_2_nonterminal(nonTerminalCounter - 1);
        // Change the start symbol to S00
        grammar.put("S00", grammar.get(start_symbol));
        grammar.remove(start_symbol);

        // Check grammar by decompressing and compare
        // SLP_to_text decompresser = new SLP_to_text();
        // String decompressed_string = decompresser.GtoT(grammar);
        // System.out.println(decompressed_string);
        // if (decompressed_string.equals(input))
        // System.out.println("YES");
        // Return the constructed grammar
        return grammar;
    }

    public Map<String, Pair<String, String>> compress(String input, ArrayList<Pair<Integer, Integer>> factorization) {
        int w = input.length();
        // Initialise tables start, end and pair
        int[] start = new int[w]; // stores beginning of factors, -1: not the beginning of a factor
        int[] end = new int[w]; // stores ends of factors: 1/-1 -> whether w[i] is the last letter of a factor
        int[] pair = new int[w]; // stores pairing, 0: unpaired; 1: first in a pair; 2: second in a pair
        Arrays.fill(start, -1);
        Arrays.fill(end, -1);
        Arrays.fill(pair, 0);
        // System.out.println(" ");
        // System.out.println("Start: " + Arrays.toString(start));
        // System.out.println("End: " + Arrays.toString(end));
        // System.out.println("Pair: " + Arrays.toString(pair));
        // System.out.println("------------------------------");

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

        // Main loop
        while (input_array.length > 1) {
            // Compute a pairing of w using pairing()
            pairing(input_array, start, end, pair);

            // Replace the pairs using pairReplacement()
            input_array = pairReplacement(input_array, start, end, pair);
            // System.out.println("pairing: input: " + String.join("", input_array));
        }

        // Change the start symbol to S00
        String start_symbol = long_2_nonterminal(nonTerminalCounter - 1);
        grammar.put("S00", grammar.get(start_symbol));
        grammar.remove(start_symbol);

        return grammar;

    }

    private void pairing(String[] input, int[] start, int[] end, int[] pair) {
        int w = input.length;
        // System.out.println(input.length);
        // System.out.println("pairing: input: " + String.join("", input));
        // System.out.println("Start: " + Arrays.toString(start));
        // System.out.println("End: " + Arrays.toString(end));
        // System.out.println("Pair: " + Arrays.toString(pair));

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
                if (start[i] == -1) { // w[i] is a free lettero
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

    private String[] pairReplacement(String[] input, int[] start, int[] end, int[] pair) {
        String[] inputP = new String[input.length]; // the new word after replacing the pairing

        // System.out.println("Replace: ----------------------------------");
        // System.out.println("Start: " + Arrays.toString(start));
        // System.out.println("End: " + Arrays.toString(end));
        // System.out.println("Pair: " + Arrays.toString(pair));
        // System.out.println("================" + String.join("", input));

        int[] newpos = new int[input.length]; //
        Arrays.fill(newpos, -1);

        int i = 0;
        int iP = 0; // iP is the position corresponding to i in the new word
        int jP = 0;
        while (i < input.length) {

            // System.out.println("i = " + i + ", input[i] = " + input[i]);
            if (start[i] != -1) { // w[i] is the first element of a factor

                start[iP] = newpos[start[i]];
                jP = newpos[start[i]]; // factor in the new word begins at the position corresponding to the
                // beginning of the current factor
                start[i] = -1;

                do {
                    newpos[i] = iP; // position corresponding to i
                    inputP[iP] = inputP[jP]; // copy the letter according to new factorization
                    // System.out.println("copy the : " + String.join("", input));
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
                        System.out.println("nonTerminal: " + nonTerminal);
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

    public static void main(String[] args) {
        String file;
        while (true) {
            // Display options
            System.out.println("1 - text to succinct grammar");
            System.out.println("2 - text to cfg");
            System.out.println("3 - cfg to succinct grammar");
            System.out.println("4 - cfg to grammar tree");
            System.out.println("5 - succinct grammar to text");
            System.out.println("6 - cfg to text");
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
                    // text -> cfg -> enc
                    Text_2_SLP t = new Text_2_SLP();
                    t.text_2_slp(false);
                    break;
                }
                case '2': {
                    Text_2_SLP t = new Text_2_SLP();
                    t.text_2_slp(true);
                    break;
                }
                case '3': {
                    // cfg -> enc
                    System.out.println("Choose the file to convert: ");
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
                        // Get input cfg
                        HashMap<String, Pair<String, String>> cfg = new ParseCFG(file).getCFG();
                        // Get the file name
                        file = file.substring(0, file.length() - 4);
                        // Encode cfg
                        CFG_2_POPPT cfg2poppt = new CFG_2_POPPT();
                        cfg2poppt.cfg2poppt(cfg, file);
                        break;
                    }
                }
                case '4': {
                    // cfg -> tree
                    System.out.println("Choose the file to convert: ");
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
                case '5': {
                    // Get input file
                    System.out.println("Choose the file to convert: ");
                    file = slp.Main.inputScanner.nextLine();
                    // Check if file exists
                    if (!new File(file).isFile()) {
                        System.out.println("Error: File does not exist.");
                        break;
                    }
                    // Check file format
                    if (!file.substring(file.length() - 4, file.length()).equals(".slp")) {
                        System.out.println("Wrong file type, please select a file of type .slp");
                    } else {
                        // poppt -> text
                        POPPT_2_TEXT p = new POPPT_2_TEXT();
                        p.poppt_2_text(new ParsePOPPT().parsePOPPT(file), file.substring(0, file.length() - 4));
                    }
                    break;
                }
                case '6': {
                    // Get input file
                    System.out.println("Choose the file to convert: ");
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
                        s.toText(new ParseCFG(file).getCFG());
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