package slp.util;

import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.Stack;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Encode a context free grammar(CFG) into a post-order partial parse
 * tree(POPPT).
 * 
 * @author Tianlong Zhong
 */
public class CFG_2_POPPT {
    public static LinkedList<String> poppt;

    public CFG_2_POPPT() {
        poppt = new LinkedList<>();
    }

    /**
     * Encode the input grammmar into POPPT.
     * 
     * @param cfg input context free grammar
     * @return Post-order partial parse tree
     */
    public LinkedList<String> cfg_2_poppt(Map<String, Pair<String, String>> cfg) {
        // Build a POPPT out of the POSLP
        LinkedList<Byte> bitStream = new LinkedList<>();
        ArrayList<String> innerNodes = new ArrayList<>();
        ArrayList<Boolean> inner_appeared_twice = new ArrayList<>();
        Stack<String> s = new Stack<String>();
        s.add("S00"); // add the start symbol
        while (!s.isEmpty()) {
            String current_node = s.peek();
            if (cfg.containsKey(current_node)) { // apply the known rule
                innerNodes.add(current_node);
                inner_appeared_twice.add(false);
                Pair<String, String> rhs = cfg.get(current_node);
                s.pop();
                s.add(rhs.second);
                s.add(rhs.first);
                // Remove the applied rule
                cfg.remove(current_node);
            } else { // repeated nonterminals, or leave terminals
                s.pop();
                if (innerNodes.contains(current_node) && !inner_appeared_twice.get(innerNodes.indexOf(current_node))) {
                    inner_appeared_twice.set(innerNodes.indexOf(current_node), true);
                    bitStream.add((byte) 1);
                } else {
                    poppt.add(current_node);
                    bitStream.add((byte) 0);
                }
            }
        }
        bitStream.add((byte) 1); // add an extra virtual node
        StringBuilder bitString = new StringBuilder(); // convert the bit stream into a string
        for (Byte b : bitStream) {
            bitString.append(b);
        }
        poppt.addFirst(bitString.toString()); // add the bit stream to the encoding

        return poppt;
    }

    /**
     * Encode the input CFG and store into the file.
     * 
     * @param cfg  input CFG
     * @param file file name
     */
    public void cfg_2_poppt(Map<String, Pair<String, String>> cfg, String file) {
        try {
            cfg_2_poppt(cfg);

            // Output encoding to the file
            FileOutputStream fos = new FileOutputStream(file + ".slp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(poppt);
            System.out.println("Output successfully saved to " + file + ".slp \n");
            oos.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }
    }

}
