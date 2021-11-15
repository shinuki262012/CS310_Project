import java.util.*;
import java.io.*;

public class Text_to_SLP {
    public static Queue<String> fresh_letters;
    public static ArrayList<Pair<String, String>> grammar; // resulting grammar

    /**
     * 
     * @param args
     */
    public static ArrayList<Pair<String, String>> TtoG(String input) {
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
        grammar = new ArrayList<>();
        // TODO:
        // Cosntruct the fresh letters, in the form of Ai, where i is an interger
        for (int f = 0; f < w; f++) {
            System.out.println("A" + Integer.toString(f) + " ");
            fresh_letters.add("A" + Integer.toString(f) + " ");
        }

        String[] input_array = input.split("");

        // Main loop
        while (input_array.length > 1) {
            // Compute a pairing of w using Pairing()
            Pairing(input_array, start, end, pair);

            // Replace the pairs using PairReplacement()
            input_array = PairReplacement(input_array, start, end, pair);
            System.out.println("Pairing: input: " + String.join("", input_array));

        }

        // Set the start symbol to S
        grammar.set(grammar.size() - 1, grammar.get(grammar.size() - 1).setFirst("S"));
        // Print grammar
        System.out.println("Grammar: ");
        for (int j = 0; j < grammar.size(); j++) {
            System.out.println(grammar.get(j).first + " -> " + grammar.get(j).second);
        }

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

            System.out.println("i = " + i);
            if (start[i] != -1) { // w[i] is the first element of a factor

                start[iP] = newpos[start[i]];
                jP = newpos[start[i]]; // factor in the new word begins at the position corresponding to the
                // beginning of the current factor
                start[i] = -1;

                do {
                    newpos[i] = iP; // position corresponding to i
                    input[iP] = input[jP]; // copy the letter according to new factorization
                    System.out.println(String.join("", input));
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
                    input[iP] = input[i]; // copy the unpaired letter
                    i++; // move to the right by the whole pair
                    iP++;
                } else {
                    String nonTerminal = fresh_letters.remove();
                    String rhs = input[iP] + input[iP + 1];
                    System.out.println(nonTerminal + rhs);
                    input[iP] = nonTerminal; // Paired free letters are replaced by a fresh letter
                    // Record the grammar ruls
                    Pair<String, String> rule = new Pair(nonTerminal, rhs);
                    grammar.add(rule);
                    i += 2;
                    iP += 1;
                }
            }
        }
        // trim and return the new word
        return Arrays.copyOfRange(input, 0, iP);
    }

    public static void main(String[] args) {
        // String input = "zzzzzipzip";
        // String input = "aabbabbbasdasb";

        String input = "cbsdrgksjizqhrylsgstzyjqpwkvtepbpqkydwlrkxtecmajavlwiooxgzohfegkfcnthrvemtmudekiijmmmtnfejdkpyhokribbmpmyrjzzvhfqhuhrfxvxgfhuhuj";

        TtoG(input);
    }

}