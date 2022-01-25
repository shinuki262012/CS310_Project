package SLP;

import java.io.*;
// import java.util.*;
import java.util.Scanner;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.IntPredicate;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/**
 * Grammar based compression implementation based on Artur Je˙z. "A really
 * Simple Approximation of Smallest Grammar".
 * In: Theoretical Computer Science 616 (Mar. 2014). doi:
 * 10.1007/978-3-319-07566-2_19.
 */
public class Text_to_SLP {
    public static long nonTerminalCounter = 0; // counter for nonterminals
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public static Map<String, Pair<String, String>> grammar = new HashMap<String, Pair<String, String>>();

    /**
     * Text to Grammar
     */
    public Map<String, Pair<String, String>> TtoG(String input) {
        int w = input.length();
        // Compute the LZ77 factorization of the input
        Factorization fac = new Factorization();
        ArrayList<Pair<Integer, Integer>> factorization = fac.factorization(input);
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

        // Add rules for all terminals
        Map<String, String> reversedTerminalRules = new HashMap<String, String>(); // rules of form X -> a
        for (int i = 0; i < input_array.length; i++) {
            // Check if the character has appeared in the grammar
            if (reversedTerminalRules.get(input_array[i]) == null) {
                // Add a grammar rule
                String nonTerminal = long_2_nonterminal(nonTerminalCounter);
                nonTerminalCounter++;
                if (input_array[i].equals(" "))
                    reversedTerminalRules.put("' '", nonTerminal);
                else if (input_array[i].equals("\n"))
                    reversedTerminalRules.put("\\n", nonTerminal);
                else
                    reversedTerminalRules.put(input_array[i], nonTerminal);
                input_array[i] = nonTerminal;
            } else {
                // replace the character with the nonterminal
                input_array[i] = reversedTerminalRules.get(input_array[i]);
            }
        }

        // System.out.println(reversedTerminalRules.toString());
        // Reverse the terminal rules
        for (Map.Entry<String, String> rule : reversedTerminalRules.entrySet()) {
            Pair<String, String> rhs = new Pair<String, String>(rule.getKey(), "");
            grammar.put(rule.getValue(), rhs);
        }
        // System.out.println("Rules for terminals: ");
        // for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
        // System.out.println(rule.getKey() + "->" + rule.getValue().first +
        // (rule.getValue().second == ""
        // ? (", " + rule.getValue().second)
        // : ""));
        // }

        // Main loop
        while (input_array.length > 1) {
            // Compute a pairing of w using Pairing()
            Pairing(input_array, start, end, pair);

            // Replace the pairs using PairReplacement()
            input_array = PairReplacement(input_array, start, end, pair);
            // System.out.println("Pairing: input: " + String.join("", input_array));
        }

        String start_symbol = long_2_nonterminal(nonTerminalCounter - 1);
        // Change the start symbol to S00
        grammar.put("S00", grammar.get(start_symbol));
        grammar.remove(start_symbol);

        // Print grammar
        System.out.println("Final grammar: ");
        for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
            String L = rule.getValue().first;
            String R = rule.getValue().second;
            String result = rule.getKey() + "->" + L;
            if (!R.equals("")) {
                result += R;
            }
            System.out.println(result);
        }

        // Check grammar by decompressing and compare
        // SLP_to_text decompresser = new SLP_to_text();
        // String decompressed_string = decompresser.GtoT(grammar);
        // System.out.println(decompressed_string);
        // if (decompressed_string.equals(input))
        // System.out.println("YES");
        // Return the constructed grammar
        return grammar;
    }

    public void Pairing(String[] input, int[] start, int[] end, int[] pair) {
        int w = input.length;
        // System.out.println(input.length);
        // System.out.println("Pairing: input: " + String.join("", input));
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

    public String[] PairReplacement(String[] input, int[] start, int[] end, int[] pair) {
        // TODO space complexity increased
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
                        nonTerminalCounter++;
                        Pair<String, String> rhs = new Pair<String, String>(input[i], input[i + 1]);
                        inputP[iP] = nonTerminal; // Paired free letters are replaced by a fresh letter
                        // System.out.println("1 input: " + String.join("", input));
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
     * Calculate the order of the input nonterminal
     * 
     * @param nonterminal
     * @return an integer representing the nonterminal
     */
    public long nonterminal_2_long(String nonterminal) {
        String alphabet = String.valueOf(nonterminal.charAt(0));
        int a = (int) Integer.valueOf(Arrays.asList(alphabets).indexOf(alphabet));
        // System.out.println(a);
        long n = (long) Integer.valueOf(nonterminal.substring(1));
        // System.out.println(n);
        return n * 26 + a;
    }

    /**
     * Get the nonterminal represented by the given integer
     * 
     * @param x an integer
     * @return the nontermial representing the integer
     */
    public String long_2_nonterminal(long x) {
        int n = (int) x / 26;
        return alphabets[(int) x % 26] + String.valueOf(n);
    }

    public static void main(String[] args) {
        Text_to_SLP t = new Text_to_SLP();
        String input = "zzzzzipzip";
        // String input = "aabbabbbasdasb";
        // String input = "aabbabbbasdaassbacdgkl"; // tests Z0 as the start symbol
        // String input = "this is a test string";
        // String input =
        // "cbsdrgksjizqhrylsgstzyjqpwkvtepbpqkydwlrkxtecmajavlwiooxgzohfegkfcnthrvemtmudekiijmmmtnfejdkpyhokribbmpmyrjzzvhfqhuhrfxvxgfhuhuj";
        // String input = "aaaaaaaaaaaa";

        // t.TtoG(input);
        while (true) {
            // Display options
            System.out.println("1 - text to succinct grammar");
            System.out.println("2 - text to cfg");
            System.out.println("3 - cfg to succinct grammar");
            System.out.println("4 - cfg to grammar tree");
            System.out.println("5 - succinct grammar to text");
            System.out.println("6 - cfg to text");
            System.out.println("q - Exit");
            Scanner s = new Scanner(System.in);
            String inputs = s.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given\n");
                break;
            }
            switch (inputs.charAt(0)) {
                case '1':
                    break;
                case '2':
                    break;
                case '3':
                    break;
                case '4':
                    break;
                case '5':
                    break;
                case '6':
                    break;
                case 'q':
                    break;
            }
        }

    }

}