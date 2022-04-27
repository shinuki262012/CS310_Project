package slp.lz77_slp;

import slp.util.*;

import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Convert the SLP into LZ77 factorization
 * 
 * @author Tianlong Zhong
 */
public class SLP_2_LZ77 {
    // SLP as CFG
    public Map<String, Pair<String, String>> cfg = new HashMap<String, Pair<String, String>>();
    // LZ77 factorization
    public ArrayList<Pair<Integer, Integer>> lz77 = new ArrayList<Pair<Integer, Integer>>();
    // store the length of the substring represented by the nonterminal
    public Map<String, Integer> subLengths = new HashMap<String, Integer>();
    // store the index of first occurence of nonterminals
    public Map<String, Integer> indexs = new HashMap<String, Integer>();
    // store the nonterminals that are back references
    public ArrayList<String> backReferences = new ArrayList<>();

    public void slp_2_lz77(String file) {
        try {
            // Parse input CFG
            cfg = new ParseCFG(file).getCFG();

            // Calculate the length of the substirngs
            getSubLengths();

            ArrayList<String> lz = new ArrayList<>(); // hold the expansion
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

            /* Output the LZ77 factorization */
            try {
                PrintWriter printWriter = new PrintWriter(file.substring(0, file.length() - 4) + ".lz77");
                for (int i = 0; i < lz77.size(); i++) {
                    printWriter.println(lz77.get(i).first + ", " + lz77.get(i).second);
                }
                System.out.println("Output successfully saved to " + file.substring(0, file.length() - 4) + ".lz77 \n");
                printWriter.close();
            } catch (Exception e) {
                System.out.println("Failed to save the output");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read the file input");
        }

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

    public String decompressLZ77() {
        StringBuffer sb = new StringBuffer();
        for (Pair<Integer, Integer> p : this.lz77) {
            if (p.second == 0) {
                sb.append((char) (int) p.first);
            } else {
                for (int i = 0; i < p.second; i++) {
                    char nextChar = sb.charAt(p.first + i);
                    sb.append(nextChar);
                }
            }
        }
        return new String(sb);
    }

    private void decompressLZ77(ArrayList<Pair<Integer, Integer>> lz77encoding, String file) {
        try {
            PrintWriter outputWriter = new PrintWriter(file + "(1)");
            StringBuffer sb = new StringBuffer();
            for (Pair<Integer, Integer> p : lz77encoding) {
                if (p.second == 0) {
                    sb.append((char) (int) p.first);
                } else {
                    for (int i = 0; i < p.second; i++) {
                        char nextChar = sb.charAt(p.first + i);
                        sb.append(nextChar);
                    }
                }
            }
            outputWriter.print(new String(sb));
            System.out.println("Output successfully saved to " + file + "(1) \n");
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save the output");
        }
    }

    public void main(String[] args) {
        while (true) {
            // Display options
            System.out.println("1 - slp to lz77");
            System.out.println("2 - decompress lz77");
            System.out.println("q - Exit");
            String inputs = slp.Main.inputScanner.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given\n");
                break;
            }
            if (inputs.charAt(0) == 'q') {
                System.out.println("Exiting...\n");
                break;
            }
            if (inputs.charAt(0) == '1') {
                System.out.println("Choose a .cfg file to convert: ");
                String file = slp.Main.inputScanner.nextLine().toString().trim();
                try {
                    if (file.isEmpty()) {
                        throw new GeneralException("No file was given");
                    }
                    if (!new File(file).isFile()) {
                        throw new GeneralException("Error: input file does not exist: " + file);
                    }
                    if (new File(file).isDirectory()) {
                        throw new GeneralException("Error: input is a directory: " + file);
                    }
                    new SLP_2_LZ77().slp_2_lz77(file);
                } catch (GeneralException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            } else if (inputs.charAt(0) == '2') {
                System.out.println("Choose a .lz77 file to decompress:");
                String file = slp.Main.inputScanner.nextLine().toString().trim();
                if (file.isEmpty()) {
                    System.out.println("No file was given.");
                    break;
                }
                if (!new File(file).isFile()) {
                    System.out.println("Error: input file does not exist: " + file);
                    break;
                }
                if (new File(file).isDirectory()) {
                    System.out.println("Error: input is a directory: " + file);
                    break;
                }
                decompressLZ77(new ParseLZ77encoding(file).getLZ77(), file.substring(0, file.length() - 5));
            } else {
                System.out.println("Please enter a valid option. \n");
                break;
            }
        }
    }
}
