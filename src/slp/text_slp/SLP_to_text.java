package slp.text_slp;

import java.io.*;
import java.util.ArrayList;
import java.util.Map; // to store the grammar

import slp.util.BinaryTreeNode;
import slp.util.Pair;

public class SLP_to_text {

    public static String GtoT(Map<String, Pair<String, String>> grammar) {
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

    public static void main(String[] args) {
        return;
    }

}