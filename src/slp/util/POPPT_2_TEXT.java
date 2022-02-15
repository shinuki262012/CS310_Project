package slp.util;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Stack;
import java.io.PrintWriter;

public class POPPT_2_TEXT {
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public POPPT_2_TEXT() {

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

    public void poppt_2_text(LinkedList<String> encoding, String file) {
        System.out.println("encoding: " + encoding.toString());
        // Restore the bit stream
        LinkedList<Byte> bit_stream = new LinkedList<>();
        for (String s : encoding.removeFirst().split("")) {
            bit_stream.add(Byte.parseByte(s));
        }
        int c = 0; // counter for bits '0' and '1'
        long i = 0; // counter for nonterminals
        HashMap<String, Pair<String, String>> Dict = new HashMap<String, Pair<String, String>>();
        Stack<String> S = new Stack<String>();
        try {
            String original_text = "";
            PrintWriter outputWriter = new PrintWriter(file + "(1)");
            while (!bit_stream.isEmpty()) {
                byte bit = bit_stream.poll();
                if (bit == 0) { // leaf node
                    c++;
                    String leaf = encoding.poll();
                    S.add(leaf);
                    if (leaf.length() == 1) { // terminals
                        original_text += leaf;
                        outputWriter.print(leaf);
                    } else { // nonterminals
                        // Recover subtext using Dict
                        Stack<String> stack1 = new Stack<String>();
                        stack1.add(leaf);
                        while (!stack1.isEmpty()) {
                            String current_node = stack1.pop();
                            if (Dict.containsKey(current_node)) { // apply the production rule for a nonterminal
                                Pair<String, String> rhs = Dict.get(current_node);
                                System.out.println("first:|" + rhs.first + "|second|" + rhs.second + "|");
                                stack1.add(rhs.first);
                                stack1.add(rhs.second);
                            } else { // append to the text for a terminal
                                original_text += current_node;
                                outputWriter.print(current_node);
                            }
                        }
                    }
                    System.out.println("original text so far: " + original_text + "|");
                } else { // internal node
                    c--;
                    if (c > 0) {
                        String lhs = S.pop();
                        String rhs = S.pop();
                        Pair<String, String> rule = new Pair<String, String>(lhs, rhs);
                        String nonterminal = long_2_nonterminal(i);
                        Dict.put(nonterminal, rule); // record the production rule in the phrase dictionary
                        i++;
                        S.add(nonterminal);
                    }
                }

            }
            System.out.println("Output successfully saved to " + file + "(1) \n");

            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }
    }
}
