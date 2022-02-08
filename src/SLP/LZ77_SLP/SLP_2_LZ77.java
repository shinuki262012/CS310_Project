package SLP.LZ77_SLP;

import SLP.BinaryTreeNode;
import SLP.Pair;
import SLP.util.ParseCFG;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import javax.xml.transform.Templates;

public class SLP_2_LZ77 {
    // SLP as CFG
    public static Map<String, Pair<String, String>> cfg = new HashMap<String, Pair<String, String>>();
    // LZ77 factorization
    public static ArrayList<Pair<Integer, Integer>> lz77 = new ArrayList<Pair<Integer, Integer>>();
    // store the length of the substring represented by the nonterminal
    public static Map<String, Integer> subLengths = new HashMap<String, Integer>();
    // store the index of first occurence of nonterminals
    public static Map<String, Integer> indexs = new HashMap<String, Integer>();
    // store the nonterminals that are back references
    public static ArrayList<String> backReferences = new ArrayList<>();

    public void SLP2LZ77() {
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("Choose the SLP file to convert: ");
        String file = inputScanner.nextLine();
        try {
            // Parse input CFG
            cfg = new ParseCFG(file).getCFG();

            // Calculate the length of the substirngs
            getSubLengths();
            for (Map.Entry<String, Integer> a : subLengths.entrySet()) {
                System.out.println(a.getKey() + " " + a.getValue());
            }

            ArrayList<String> lz = new ArrayList<>();
            lz.add("S00");
            boolean terminate = false;
            int maxLength = 0;
            String instance = ""; // nonterminal with the longest expansions
            int instanceIndex = 0; // index of the instance
            int curIdx = 0;
            while (!terminate) {
                terminate = true;
                maxLength = 0;
                curIdx = 0;
                // Select the nonterminal with the longest expansion(width)
                for (int i = 0; i < lz.size(); i++) {
                    String currentItem = lz.get(i);
                    if (cfg.containsKey(currentItem) && subLengths.get(currentItem) > maxLength) {
                        maxLength = subLengths.get(currentItem);
                        instanceIndex = i;
                        instance = currentItem;
                        indexs.put(instance, curIdx);
                    }
                    if (subLengths.containsKey(currentItem)) {
                        curIdx += subLengths.get(currentItem);
                    } else {
                        String nonterminal = currentItem.substring(0, currentItem.indexOf("_"));
                        curIdx += subLengths.get(nonterminal);
                    }
                }
                System.out.println("instance:" + instance);
                // Replace the left most instance with its definition
                if (cfg.get(instance).second == null) { // A -> a
                    lz.set(instanceIndex, cfg.get(instance).first);
                    // Replace subsequent occurence with reference to the first instance
                    for (int k = instanceIndex; k < lz.size(); k++) {
                        if (lz.get(k).equals(instance)) {
                            lz.set(k, instance + "_" + subLengths.get(instance));
                        }
                    }
                } else { // A -> BC
                    lz.set(instanceIndex, cfg.get(instance).first);
                    String tmp1, tmp2;
                    if (lz.size() == instanceIndex + 1) { // last item
                        lz.add(cfg.get(instance).second);
                    } else {
                        tmp1 = lz.get(instanceIndex + 1);
                        lz.set(instanceIndex + 1, cfg.get(instance).second);
                        for (int j = instanceIndex + 2; j < lz.size(); j++) {
                            tmp2 = lz.get(j);
                            lz.set(j, tmp1);
                            tmp1 = tmp2;
                        }
                        lz.add(tmp1);
                    }
                    // Replace subsequent occurence with reference to the first instance
                    for (int k = instanceIndex + 1; k < lz.size(); k++) {
                        if (lz.get(k).equals(instance)) {
                            lz.set(k, instance + "_" + subLengths.get(instance));
                        }
                    }
                }

                // Check if any nonterminal remains
                for (int i = 0; i < lz.size(); i++) {
                    if (cfg.containsKey(lz.get(i))) {
                        terminate = false;
                        break;
                    }
                }
                System.out.println(lz.toString());
            }

            // Get indexs of back references
            for (Map.Entry<String, Integer> a : indexs.entrySet()) {
                System.out.println(a.getKey() + " " + a.getValue());
            }
            String nonterminal, currentFactor;

            // Construct the LZ77 factorization
            for (int i = 0; i < lz.size(); i++) {
                currentFactor = lz.get(i);
                if (currentFactor.length() == 1) { // single character
                    lz77.add(new Pair<Integer, Integer>((int) currentFactor.charAt(0), 0));
                } else { // back reference
                    // parse factor
                    nonterminal = currentFactor.substring(0, currentFactor.indexOf("_"));
                    lz77.add(new Pair<Integer, Integer>((int) indexs.get(nonterminal), subLengths.get(nonterminal)));
                }
            }
            for (int i = 0; i < lz77.size(); i++) {
                System.out.println(lz77.get(i).first + ", " + lz77.get(i).second);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputScanner.close();
    }

    private void getSubLengths() {
        Stack<BinaryTreeNode<String>> treeStack = new Stack<>();
        String currentValue;
        BinaryTreeNode<String> start = new BinaryTreeNode<String>("S00");
        treeStack.add(start);
        while (!treeStack.isEmpty()) {
            currentValue = treeStack.peek().getValue();
            if (cfg.containsKey(currentValue)) {
                BinaryTreeNode<String> left = new BinaryTreeNode<String>(cfg.get(currentValue).first);
                BinaryTreeNode<String> right = new BinaryTreeNode<String>(cfg.get(currentValue).second);
                treeStack.peek().setLeft(left);
                treeStack.peek().setRight(right);
                treeStack.pop();
                treeStack.add(right);
                treeStack.add(left);
            } else {
                treeStack.pop();
            }
        }

        // Populate subLength
        start.updateHeight();
        start.updateWidth();
        dfs(start);
    }

    private void dfs(BinaryTreeNode<String> node) {
        if (node != null) {
            this.subLengths.put(node.getValue(), node.getWidth());
            dfs(node.getLeft());
            dfs(node.getRight());
        }
    }

    public static void main(String[] args) {
        new SLP_2_LZ77().SLP2LZ77();
    }
}
