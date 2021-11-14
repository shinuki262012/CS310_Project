import java.util.*;
import java.io.*;
import java.lang.reflect.Array;

public class Text_to_SLP {
    /**
     * 
     * @param args
     */
    public static void TtoG(String input) {
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

            if (second == 0) { // free letter
                start[curIdx] = curIdx;
                end[curIdx] = 1;
                curIdx += 1;
            } else {
                start[curIdx] = first;
                curIdx += second;
                end[curIdx - 1] = 1;
            }
        }
        System.out.println("Start: " + Arrays.toString(start));
        System.out.println("End: " + Arrays.toString(end));
        System.out.println("------------------------------");

        System.out.println("------------------------------");
        while (input.length() > 1) {
            // Compute a pairing of w using Pairing()
            Pairing(input, start, end, pair);

            // Replace the pairs using PairReplacement()
            PairReplacement(input, start, end, pair);
        }
        // Return the constructed grammar

    }

    public static void Pairing(String input, int[] start, int[] end, int[] pair) {
        System.out.println("Start: " + Arrays.toString(start));
        System.out.println("End: " + Arrays.toString(end));
        System.out.println("Pair: " + Arrays.toString(pair));
        int w = input.length();
        System.out.println("Pairing: input length: " + w);

        int i = 1;

        int j = 0;
        pair[0] = 0;
        while (i < w) {
            System.out.println(i);
            if (start[i] != -1) { // w[i] is the first element of a factor
                if (end[i] == 1) { // This is a one-letter factor
                    System.out.println("one letter factor");
                    start[i] = end[i] = -1; // Turn this letter into a free letter
                } else if (start[i] == i - 1) { // the factor is a^k, its definition begins one position to the left
                    System.out.println("a^k");
                    start[i + 1] = i - 1; // Move the definition of the factor
                    start[i] = -1; // make w[i] a free letter
                } else if (pair[start[i]] != 1) { // The pairing of the definition of factor is bad:
                    System.out.println("bad factor");
                    start[i + 1] = start[i] + 1; // shorten the factor definition by 1 on the left
                    start[i] = -1; // make a free letter
                } else { // Good factor
                    System.out.println("good factor");
                    System.out.println("j is :" + start[i]);
                    j = start[i]; // factor's definition starts at position j
                    do { // copy the pairing from the factor definition
                        System.out.println("pair is " + pair[j]);
                        System.out.println("end is " + end[i]);

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
                System.out.println("a free letter");

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

    public static void PairReplacement(String input, int[] start, int[] end, int[] pair) {
        System.out.println("Start: " + Arrays.toString(start));
        System.out.println("End: " + Arrays.toString(end));
        System.out.println("Pair: " + Arrays.toString(pair));
        char[] new_word = new char[input.length()];
        char[] input_word = input.toCharArray();

        int[] newpos = new int[input.length()]; //
        Arrays.fill(newpos, -1);

        int i = 0;
        int iP = 0; // iP is the position corresponding to i in the new word
        int jP = 0;
        while (i < input.length()) {

            System.out.println("i = " + i);
            if (start[i] != -1) { // w[i] is the first element of a factor
                System.out.println("not a free letter");

                start[iP] = jP = newpos[start[i]]; // factor in the new word begins at the position corresponding to the

                // beginning of the current factor
                start[i] = -1;
                do {
                    newpos[i] = iP; // position corresponding to i
                    new_word[iP] = input_word[jP]; // copy the letter according to new factorization
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
                System.out.println("free letter");
                newpos[i] = iP;
                if (pair[i] == 0) {
                    new_word[iP] = input_word[i]; // copy the unpaired letter
                    i++; // move to the right by the whole pair
                    iP++;
                } else {
                    System.out.println("replace");
                    new_word[iP] = 'A'; // Paired free letters are replaced by a fresh letter
                    i += 2;
                    iP += 1;
                }
            }
        }
        input = String.valueOf(new_word);
        System.out.println("input is: " + input);
        return;

    }

    /**
     * Generate fresh letters for grammar
     * 
     * @param w number of fresh letters
     */
    private static void freshLetter(int w) {

    }

    public static void main(String[] args) {
        String input = "zzzzzipzip";
        // String input = "aabbabbbasdasb";

        // String input =
        // "cbsdrgksjizqhrylsgstzyjqpwkvtepbpqkydwlrkxtecmajavlwiooxgzohfegkfcnthrvemtmudekiijmmmtnfejdkpyhokribbmpmyrjzzvhfqhuhrfxvxgfhuhuj";

        TtoG(input);
    }

}