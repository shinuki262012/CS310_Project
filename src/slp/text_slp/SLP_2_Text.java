package slp.text_slp;

import slp.util.BinaryTreeNode;
import slp.util.Pair;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

/**
 * Decompress SLP to string/parse tree
 * 
 * @author Tianlong Zhong
 */
public class SLP_2_Text {

    public String GtoT(Map<String, Pair<String, String>> grammar) {
        ArrayList<String> text = new ArrayList<>();
        ArrayList<String> newText = new ArrayList<>();
        ArrayList<BinaryTreeNode<String>> layer = new ArrayList<>();
        ArrayList<BinaryTreeNode<String>> next_layer = new ArrayList<>();

        text.add("S"); // add the start symbol
        BinaryTreeNode<String> root = new BinaryTreeNode<String>("S");
        layer.add(root);
        boolean terminate = false;
        while (!terminate) {
            terminate = true; // terminate if no rules to be applied
            for (int i = 0; i < text.size(); i++) {
                String nonTerminal = text.get(i);
                if (grammar.get(nonTerminal) != null) { // apply the production rule
                    // Apply the production rule
                    String first = grammar.get(nonTerminal).first;
                    String second = grammar.get(nonTerminal).second;
                    newText.add(first);

                    if (second != "") {
                        newText.add(second);
                    }

                    // Add children the the parent node
                    BinaryTreeNode<String> left = new BinaryTreeNode<String>(first);
                    BinaryTreeNode<String> right = new BinaryTreeNode<String>(second);
                    layer.get(i).setLeft(left);
                    next_layer.add(left);
                    if (second != "") {
                        layer.get(i).setRight(right);
                        next_layer.add(right);
                    }

                    terminate = false;
                } else {
                    next_layer.add(layer.get(i));
                    newText.add(nonTerminal); // copy the terminal
                }
            }
            if (!terminate) {
                text.clear();
                text.addAll(newText);
                newText.clear();
                layer.clear();
                layer.addAll(next_layer);
                next_layer.clear();
            }
        }

        System.out.println("Parse tree: ");
        root.toString("", false);
        return String.join("", newText);
    }

    /**
     * Print parse tree of the input SLP
     * 
     * @param grammar SLP
     */
    public void toTree(Map<String, Pair<String, String>> grammar) {
        Stack<BinaryTreeNode<String>> treeStack = new Stack<>();
        String currentValue;
        BinaryTreeNode<String> root = new BinaryTreeNode<String>("S00");
        treeStack.add(root);
        while (!treeStack.isEmpty()) {
            currentValue = treeStack.peek().getValue();
            if (grammar.containsKey(currentValue)) {
                BinaryTreeNode<String> left = new BinaryTreeNode<String>(grammar.get(currentValue).first);
                BinaryTreeNode<String> right = new BinaryTreeNode<String>(grammar.get(currentValue).second);
                treeStack.peek().setLeft(left);
                treeStack.peek().setRight(right);
                treeStack.pop();
                treeStack.add(right);
                treeStack.add(left);
            } else {
                treeStack.pop();
            }
        }

        root.toString("", false);
    }

    /**
     * Decompress the input SLP to text
     * 
     * @param grammar SLP
     * @return string represented by the SLP
     */
    public String toText(Map<String, Pair<String, String>> grammar) {
        Stack<String> s = new Stack<String>();
        String originalText = "";
        String currentNode;
        s.add("S00"); // add start symbol
        while (!s.isEmpty()) {
            currentNode = s.pop();
            if (grammar.containsKey(currentNode)) {
                s.add(grammar.get(currentNode).second);
                s.add(grammar.get(currentNode).first);
            } else {
                originalText += currentNode;
            }
        }
        // System.out.println(originalText);
        return originalText;
    }

    /**
     * Decompress the input SLP and save the output to the given file
     * 
     * @param grammar SLP
     * @param file    file to save the output
     */
    public void toText(Map<String, Pair<String, String>> grammar, String file) {
        Stack<String> s = new Stack<String>();
        String currentNode;
        s.add("S00"); // add start symbol
        try {
            PrintWriter outputWriter = new PrintWriter(file + "(1)");
            while (!s.isEmpty()) {
                currentNode = s.pop();
                if (grammar.containsKey(currentNode)) {
                    s.add(grammar.get(currentNode).second);
                    s.add(grammar.get(currentNode).first);
                } else {
                    outputWriter.print(currentNode);
                }
            }
            System.out.println("Output successfully saved to " + file + "(1) \n");
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }

    }

}
