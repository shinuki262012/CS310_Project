import java.io.*;
// import java.util.*;
import java.util.Queue;
import java.util.TreeMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map; // to store the grammar
import java.util.HashMap;

/**
 * int -> long
 * 
 *
 * 
 */

public class Text_to_SLP {
    public static Queue<String> fresh_letters;
    public static Map<String, Pair<String, String>> grammar = new HashMap<String, Pair<String, String>>(); // resulting

    /**
     * Text to Grammar
     * 
     * @param input String input
     * @return An array list representation of the grammar
     */
    public static Map<String, Pair<String, String>> TtoG(String input) {
        int w = input.length();
        // Compute the LZ77 factorization of the input
        Factorization fac = new Factorization();
        ArrayList<Pair<Integer, Integer>> factorization = fac.factorization(input);
        // Initialise tables start, end and pair
        int[] start = new int[w]; // stores beginning of factors, -1::not the beginning of a factor
        int[] end = new int[w]; // stores ends of factors: whether w[i] is the last letter of a factor, 1/-1
        int[] pair = new int[w]; // stores pairing, 0: unpaired; 1: first in a pair; 2: second in a pair
        Arrays.fill(start, -1);
        Arrays.fill(end, -1);
        Arrays.fill(pair, 0);
        System.out.println(" ");
        System.out.println("Start: " + Arrays.toString(start));
        System.out.println("End: " + Arrays.toString(end));
        System.out.println("Pair: " + Arrays.toString(pair));
        System.out.println("------------------------------");

        // Populate tables with the factorization
        int curIdx = 0;
        System.out.println("LZ77 factorization size: " + factorization.size());
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

        fresh_letters = new LinkedList<>();
        String[] alphabets = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z" };

        // TODO for large files?
        // 26 letters * 2^32?
        // Cosntruct the fresh letters, in the form of Xi, where i is an interger
        for (int f = 0; f < 2 * w; f++) {
            int n = (int) f / 26;
            System.out.println(alphabets[f % 26] + Integer.toString(n));
            fresh_letters.add(alphabets[f % 26] + Integer.toString(n));
        }

        String[] input_array = input.split("");

        // Add rules for all terminals
        Map<String, String> terminalRules = new HashMap<String, String>(); // rules of form X -> a
        for (int i = 0; i < input_array.length; i++) {
            // Check if the character has appeared in the grammar
            if (terminalRules.get(input_array[i]) == null) {
                // Add a grammar rule
                String nonTerminal = fresh_letters.remove();
                terminalRules.put(input_array[i], nonTerminal);
                input_array[i] = nonTerminal;
            } else {
                // replace the character with the nonterminal
                input_array[i] = terminalRules.get(input_array[i]);
            }
        }

        System.out.println(terminalRules.toString());
        // Reverse the terminal rules
        for (Map.Entry<String, String> rule : terminalRules.entrySet()) {
            Pair<String, String> rhs = new Pair<String, String>(rule.getKey(), "");
            grammar.put(rule.getValue(), rhs);
        }
        String tRules = grammar.toString();
        System.out.println(tRules);

        // Main loop
        while (input_array.length > 1) {
            // Compute a pairing of w using Pairing()
            Pairing(input_array, start, end, pair);

            // Replace the pairs using PairReplacement()
            input_array = PairReplacement(input_array, start, end, pair);
            System.out.println("Pairing: input: " + String.join("", input_array));
        }

        // Set the start symbol to S
        String start_symbol;
        String last_letter = fresh_letters.remove();
        System.out.println("last leter is :" + last_letter);
        char letter = last_letter.charAt(0);
        String number = last_letter.substring(1);
        // The previous one of An is Zn-1
        if (letter == 'A') {
            Integer n = Integer.valueOf(number);
            int n1 = (int) n;
            start_symbol = "Z" + Integer.toString(n1 - 1);
            System.out.println(start_symbol);
        }
        // The previous of Bn is An
        else {
            int ascii = (int) letter;
            start_symbol = ((char) (ascii - 1)) + number;
            System.out.println(start_symbol);
        }

        // Change the start symbol to S
        grammar.put("S", grammar.get(start_symbol));
        grammar.remove(start_symbol);

        // Print grammar
        for (Map.Entry<String, Pair<String, String>> rule : grammar.entrySet()) {
            System.out.println(rule.getKey() + "->" + rule.getValue().first + ", " + rule.getValue().second);
        }

        // Check grammar by decompressing and compare
        SLP_to_text decompresser = new SLP_to_text();
        String decompressed_string = decompresser.GtoT(grammar);
        System.out.println(decompressed_string);
        if (decompressed_string.equals(input))
            System.out.println("YES");

        // Return the constructed grammar
        return grammar;
    }

    public static void Pairing(String[] input, int[] start, int[] end, int[] pair) {
        int w = input.length;
        System.out.println("Pairing: input: " + String.join("", input));
        // System.out.println("Start: " + Arrays.toString(start));
        // System.out.println("End: " + Arrays.toString(end));
        // System.out.println("Pair: " + Arrays.toString(pair));

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

        return;
    }

    public static String[] PairReplacement(String[] input, int[] start, int[] end, int[] pair) {
        System.out.println("Replace: ----------------------------------");
        // TODO space complexity increased
        String[] inputP = new String[input.length]; // the new word after replacing the pairing

        // System.out.println("Start: " + Arrays.toString(start));
        // System.out.println("End: " + Arrays.toString(end));
        // System.out.println("Pair: " + Arrays.toString(pair));
        System.out.println("================" + String.join("", input));

        int[] newpos = new int[input.length]; //
        Arrays.fill(newpos, -1);

        int i = 0;
        int iP = 0; // iP is the position corresponding to i in the new word
        int jP = 0;
        while (i < input.length) {

            System.out.println("i = " + i + ", input[i] = " + input[i]);
            if (start[i] != -1) { // w[i] is the first element of a factor

                start[iP] = newpos[start[i]];
                jP = newpos[start[i]]; // factor in the new word begins at the position corresponding to the
                // beginning of the current factor
                start[i] = -1;

                do {
                    newpos[i] = iP; // position corresponding to i
                    inputP[iP] = inputP[jP]; // copy the letter according to new factorization
                    System.out.println("copy the  : " + String.join("", input));
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
            if (start[i] == -1) { // w[i] is a free letter
                newpos[i] = iP;
                if (pair[i] == 0) {
                    inputP[iP] = input[i]; // copy the unpaired letter
                    i++; // move by this letter to the right
                    iP++;
                } else {
                    String nonTerminal = fresh_letters.remove();
                    Pair<String, String> rhs = new Pair<String, String>(input[i], input[i + 1]);
                    BinaryTreeNode<String> node = new BinaryTreeNode<String>(nonTerminal);
                    node.setLeft()
                    inputP[iP] = nonTerminal; // Paired free letters are replaced by a fresh letter
                    System.out.println("1 input: " + String.join("", input));
                    // Record the grammar ruls
                    grammar.put(nonTerminal, rhs);
                    i += 2;
                    iP += 1;
                }
            }
        }
        return Arrays.copyOfRange(inputP, 0, iP);
    }

    public static void main(String[] args) {
        String input = "zzzzzipzip";
        // String input = "aabbabbbasdasb";
        // String input = "aabbabbbasdaassbacdgkl"; // tests Z0 as the start symbol
        // String input = "this is a test string";
        // String input =
        // "cbsdrgksjizqhrylsgstzyjqpwkvtepbpqkydwlrkxtecmajavlwiooxgzohfegkfcnthrvemtmudekiijmmmtnfejdkpyhokribbmpmyrjzzvhfqhuhrfxvxgfhuhuj";

        TtoG(input);
    }

}