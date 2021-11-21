import java.io.*;
// import java.util.*;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map; // to store the grammar
import java.util.HashMap;

public class SLP_to_text {
    public static String GtoT(Map<String, Pair<String, String>> grammar) {
        ArrayList<String> text = new ArrayList<>();
        ArrayList<String> newText = new ArrayList<>();
        text.add("S"); // add the start symbol
        boolean terminate = false;
        while (!terminate) {
            terminate = true; // terminate if no rules to be applied
            for (String nonTerminal : text) {
                if (grammar.get(nonTerminal) != null) { // apply the production rule
                    // Apply the production rule
                    newText.add(grammar.get(nonTerminal).first);
                    if (grammar.get(nonTerminal).second != "")
                        newText.add(grammar.get(nonTerminal).second);
                    terminate = false;
                } else {
                    newText.add(nonTerminal); // copy the terminal
                }
            }
            if (!terminate) {
                text = new ArrayList<String>(newText);
                newText.clear();
            }
        }
        return String.join("", newText);
    }

    public static void main(String[] args) {
        return;
    }

}
