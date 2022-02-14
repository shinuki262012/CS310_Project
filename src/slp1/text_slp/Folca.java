package slp.text_slp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import slp.Main;
import slp.util.CFG_2_POPPT;
import slp.util.Pair;
import slp.util.ParseCFG;

import java.util.Iterator;
import java.util.Set;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Queue;

/**
 * Fully Online LCA implementation based on S. Maruyama and Y. Tabei,
 * "Fully Online Grammar Compression in Constant Space," 2014 Data Compression
 * Conference, 2014, pp. 173-182, doi: 10.1109/DCC.2014.69.
 *
 */
public class Folca {
    public static ArrayList<Queue<String>> queues;
    public static Map<String, Pair<String, String>> D;
    public static Map<String, Integer> freq_counter;
    public static int k;
    public static double ep;

    public static Map<String, String> D_r;
    public static long nonTerminalCounter;
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public Folca() {
        queues = new ArrayList<Queue<String>>();
        D = new HashMap<String, Pair<String, String>>(); // phrase dictionary
        D_r = new HashMap<String, String>();
        freq_counter = new HashMap<String, Integer>(); // frequency counter
        k = 1024; // max size of the pharse dictionary
        ep = 5; // vacancy rate
        nonTerminalCounter = 0; // counter for nontermials
    }

    /**
     * Fully Online LCA implementation
     * Online algorithm that compresses the input text into SLP in the form of CFG
     *
     *
     *
     * @param output_cfg whether to output a cfg or encoded cfg
     */
    public void folca(boolean output_cfg) {
        // Initialize queue with 2 dummy symbols
        Queue<String> q0 = new LinkedList<String>();
        q0.add("");
        q0.add("");
        queues.add(q0);
        System.out.println("Choose the file to convert: ");
        String file = Main.inputScanner.nextLine();
        // Check if file exists
        if (!new File(file).isFile()) {
            System.out.println("Error: File does not exist.");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            int c;
            try {
                // Read a character and process it
                while ((c = reader.read()) != -1) {
                    String character = String.valueOf((char) c);
                    processSymbol(0, character);
                }
                // Finish up
                boolean finish = false;
                while (!finish) {
                    finish = true;
                    for (int i = 0; i < queues.size() - 1; i++) {
                        if (queues.get(i).size() == 3) {
                            queues.get(i).poll();
                            queues.get(i).poll();
                            processSymbol(i + 1, queues.get(i).poll());
                            finish = false;
                        } else if (queues.get(i).size() == 4) {
                            queues.get(i).poll();
                            queues.get(i).poll();
                            String qk3 = queues.get(i).poll();
                            String qk4 = queues.get(i).poll();
                            queues.get(i).add(qk3);
                            queues.get(i).add(qk4);
                            // String Y = update(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                            String Y = freqCountingUpdate(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                            processSymbol(i + 1, Y); // add the nonterminal Y to upper tree level
                        }
                    }
                }

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Failed to read the file");
        }
        // DELETE
        for (Map.Entry<String, Pair<String, String>> rule : D.entrySet()) {
            System.out.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
        }
        for (Map.Entry<String, String> rule : D_r.entrySet()) {
            System.out.println(rule.getKey() + "<-" + rule.getValue());
        }

        // Get the starting symbol: the root node in the parse tree
        queues.get(queues.size() - 1).poll();
        queues.get(queues.size() - 1).poll();
        String start_symbol = queues.get(queues.size() - 1).poll();
        System.out.println("hi");
        System.out.println("Start symbol: " + start_symbol);
        // Replace the start symbol as S00
        D.put("S00", D.remove(start_symbol));
        D_r.put(D.get("S00").first + D.get("S00").second, "S00");

        // Output the CFG to a file
        if (output_cfg) {
            try {
                PrintWriter outputWriter = new PrintWriter(file + ".cfg");
                for (Map.Entry<String, Pair<String, String>> rule : D.entrySet()) {
                    outputWriter.println(rule.getKey() + "->" + rule.getValue().first + " " + rule.getValue().second);
                }
                System.out.println("Output successfully saved to " + file + ".cfg");
                outputWriter.close();
            } catch (Exception e) {
                System.out.println("Failed to save the output");
            }
        }
        // Encode the CFG into a succinct form and output to a file
        else {
            CFG_2_POPPT cfg2poppt = new CFG_2_POPPT();
            cfg2poppt.cfg2poppt(D, file);
        }
    }

    /**
     * Process the given character X inserted into the given level
     *
     *
     *
     * @param level the level where X is inserted into
     * @param X     input character X
     */
    public void processSymbol(int level, String X) {
        // System.out.println("Processing " + X);
        Queue<String> q_k = queues.get(level);
        q_k.add(X);
        // System.out.println(queues.toString());
        if (q_k.size() == 4) {
            if (!landmark(q_k, 1)) { // build a 2-tree: A -> XY
                if (level + 1 == queues.size()) { // the queue in the next level is not defined yet
                    // Initiate and add a new queue one level up
                    Queue<String> q_k_plus_1 = new LinkedList<String>();
                    q_k_plus_1.add("");
                    q_k_plus_1.add("");
                    queues.add(q_k_plus_1);
                }

                q_k.poll();
                q_k.poll();
                String qk3 = q_k.poll();
                String qk4 = q_k.poll();
                q_k.add(qk3);
                q_k.add(qk4);
                // String Y = update(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                String Y = freqCountingUpdate(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                processSymbol(level + 1, Y); // add the nonterminal Y to upper tree level
            }
        } else if (q_k.size() == 5) { // build a 2-2-tree: A -> YZ and B -> XA
            if (level + 1 == queues.size()) { // the queue in the next level is not defined yet
                // Initiate and add a new queue one level up
                Queue<String> q_k_plus_1 = new LinkedList<String>();
                q_k_plus_1.add("");
                q_k_plus_1.add("");
                queues.add(q_k_plus_1);
            }

            q_k.poll();
            q_k.poll();
            String qk3 = q_k.poll();
            String qk4 = q_k.poll();
            String qk5 = q_k.poll();
            q_k.add(qk4);
            q_k.add(qk5);
            // String Y = update(qk4, qk5); // replace q_k[4], q_k[5] with nonterminal Y
            // String Z = update(qk3, Y); // replace q_k[3], Y with nonterminal Z
            String Y = freqCountingUpdate(qk4, qk5); // replace q_k[4], q_k[5] with nonterminal Y
            String Z = freqCountingUpdate(qk3, Y); // replace q_k[3], Y with nonterminal Z
            processSymbol(level + 1, Z); // add nonterminal Z to upper tree level
        }
    }

    /**
     * Decide whether position pos in q_k is a landmark: the pair q_k[pos, pos+1] is
     * replaced or not
     *
     *
     *
     * @param q_k substring of length 4: q_k[pos-1, pos, pos+1, pos+2]
     * @param pos position
     * @return whether position pos in q_k is a landmark
     */
    public boolean landmark(Queue<String> q_k, int pos) {
        // System.out.println("landmark for" + q_k.toString());
        // Copy the given queue into an array
        String[] qk = new String[q_k.size()];
        for (int j = 0; j < q_k.size(); j++) {
            qk[j] = (String) q_k.remove();
            q_k.add(qk[j]);
        }

        if (qk[0] == "") { // the queue starts with dummy symbols, qk[pos, pos+1] should not be paired
            return false;
        } else {
            if (qk[pos + 1].equals(qk[pos + 2])) { // qk[pos+1, pos+2] is a repetitive pair
                return false;
            } else if (isMinimal(qk, pos) || isMaximal(qk, pos)) { // qk[pos, pos+1] is a minimal/maximal pair
                return true;
            } else { // qk[pos, pos+2] contains no special pair
                return true;
            }
        }
    }

    /**
     * queue = {i-1, i, i+1, i+2}, check if queue[i, i+1] is a minimal pair
     * queue[i, i+1] is a minimal pair if queue[i+1] < queue[i], queue[i+2]
     *
     *
     *
     * @param queue given queue of length 4
     * @param pos   given position
     * @return whether queue[i, i+1] is a minimal pair
     */
    public boolean isMinimal(String[] queue, int pos) {
        // [?, terminal, ?]
        if (queue[pos].length() == 1) {
            // [terminal, terminal, ?]
            if (queue[pos - 1].length() == 1) {
                if (queue[pos + 1].length() == 1) { // [terminal, terminal, terminal]
                    return ((((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0))) &&
                            (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0))))
                                    ? true
                                    : false;
                } else { // [terminal, terminal, nonterminal]
                    return (((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0)))
                            ? true
                            : false;
                }
            }
            // [nonterminal, terminal, terminal]
            else if (queue[pos + 1].length() == 1) {
                return (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0)))
                        ? true
                        : false;
            }
            // [nonterminal, terminal, nonterminal]
            else {
                return true;
            }
        }
        // [?, nonterminal, ?]
        else {
            // [T, nonterminal, ?] or [?, nonterminal, T]
            if (queue[pos - 1].length() == 1 || queue[pos + 1].length() == 1) {
                return false;
            }
            // [nonterminal, nonterminal, nonterminal]
            else {
                return (queue[pos].compareTo(queue[pos - 1]) < 0 &&
                        queue[pos].compareTo(queue[pos + 1]) < 0)
                                ? true
                                : false;
            }
        }
    }

    /**
     * queue = {i-1, i, i+1, i+2}, check if queue[i, i+1] is a maximal pair
     * queue[i, i+1] is a maximal pair if queue[i-1, i+2] is in an
     * incresing/decreasing order, and lca(queue[i, i+1]) > lca(queue[i-1, i]),
     * lca(queue[i+1, i+2])
     *
     *
     *
     * @param queue given queue of length 4
     * @param pos   given position
     * @return whether queue[i, i+1] is a maximal pair
     */
    public boolean isMaximal(String[] queue, int pos) {
        String s0 = queue[pos - 1];
        String s1 = queue[pos];
        String s2 = queue[pos + 1];
        String s3 = queue[pos + 2];
        // Check if the queue is in increasing/decreasing order
        if ((s0.compareTo(s1) < 0 && s1.compareTo(s2) < 0 && s2.compareTo(s3) < 0) ||
                (s0.compareTo(s1) > 0 && s1.compareTo(s2) > 0 && s2.compareTo(s3) > 0)) {
            return (((lca(s1, s2)) > lca(s0, s1)) && ((lca(s1, s2)) > lca(s2, s3)))
                    ? true
                    : false;
        } else
            return false;
    }

    /**
     * Calculate the least common ancestor of the input pair of strings.
     *
     *
     *
     * @param i character
     * @param j character
     * @return least common ancestor of (i, j) in the complete binary tree of the
     *         alphabets
     */
    public long lca(String i, String j) {
        // Terminals casted into integers
        long l_i = (i.length() == 1) ? (int) i.charAt(0) : -1;
        long l_j = (j.length() == 1) ? (int) j.charAt(0) : -1;
        // Nonterminals converted into integers
        if (l_i == -1) {
            l_i = 2 ^ 32 + nonterminal_2_long(i);
        }
        if (l_j == -1) {
            l_j = 2 ^ 32 + nonterminal_2_long(j);
        }

        l_i = 2 * l_i - 1;
        l_j = 2 * l_j - 1;
        long x = l_i ^ l_j; // bitwise exclusive or
        return (long) Math.floor(Math.log(x) / Math.log(2));
    }

    /**
     * Generate a production rule and add to the phrase dictionary
     *
     *
     *
     * @param X first symbol in pair
     * @param Y second symbol in pair
     * @return the nonterminal that produces the pair
     */
    public String update(String X, String Y) {
        Pair<String, String> rhs = new Pair<>(X, Y);
        String Z = D_r.get(X + Y);
        if (Z == null) { // check if the rhs is in the reverse dictionary
            // assign a new nonterminal
            Z = long_2_nonterminal(nonTerminalCounter);
            nonTerminalCounter++;
            D_r.put(X + Y, Z); // add the new production rule to the reverse dictionary
            if (!D.containsKey(Z)) {
                D.put(Z, rhs); // add the new production rule to the phrase dictionary
            }
        }
        return Z;
    }

    /**
     * Similar as update() but removes infrequent production rules
     *
     *
     *s
     * @param X first symbol in pair
     * @param Y second symbol in pair
     * @return the nonterminal that produces the pair
     */
    public String freqCountingUpdate(String X, String Y) {
        String Z = D_r.get(X + Y);
        if (Z != null) { // D contains the rule
            // Increment the frequency by 1
            if (freq_counter.containsKey(Z)) {
                freq_counter.replace(Z, freq_counter.get(Z) + 1);
            } else {
                freq_counter.put(Z, 1);
            }
        } else { // D does not contains the rule
            Z = long_2_nonterminal(nonTerminalCounter);
            nonTerminalCounter++;
            // If the size of the phrase dictionary reached the max size
            if (D.size() >= k) {
                // Remove infrequent rules
                while (k * (1 - ep / 100) < D.size()) {
                    Iterator iterator = D.keySet().iterator();
                    while (iterator.hasNext()) {
                        String nonterminal = (String) iterator.next();
                        freq_counter.replace(nonterminal, freq_counter.get(nonterminal) - 1);
                        if (freq_counter.get(nonterminal) == 0) {
                            iterator.remove();
                            freq_counter.remove(nonterminal);
                        }
                    }
                }
            }
            // Add the new production rule
            Pair<String, String> rhs = new Pair<>(X, Y);
            D_r.put(X + Y, Z); // add the new production rule to the reverse dictionary
            if (!D.containsKey(Z))
                D.put(Z, rhs); // add the new production rule to the phrase dictionary
            if (!freq_counter.containsKey(Z))
                freq_counter.put(Z, 1);
        }
        return Z;
    }

    /**
     * Calculate the order of the input nonterminal
     * 
     * @param nonterminal
     * @return an integer representing the nonterminal
     */
    public long nonterminal_2_long(String nonterminal) {
        String alphabet = String.valueOf(nonterminal.charAt(0));
        int a = (int) Integer.valueOf(Arrays.asList(alphabets).indexOf(alphabet));
        // System.out.println(a);
        long n = (long) Integer.valueOf(nonterminal.substring(1));
        // System.out.println(n);
        return n * 26 + a;
    }

    /**
     * Get the nonterminal represented by the given integer
     *
     * @param x an integer
     * @return the nontermial representing the integer
     */
    public String long_2_nonterminal(long x) {
        int n = (int) x / 26;
        return alphabets[(int) x % 26] + String.valueOf(n);
    }

    /**
     * Parse the input .slp file
     *
     * @param file input file name
     * @return the POPPT contained in the file
     */
    @SuppressWarnings({ "unchecked" })
    public LinkedList<String> parsePOPPT(String file) {
        LinkedList<String> encoding = new LinkedList<String>();
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            try {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                encoding = (LinkedList<String>) oin.readObject();
                oin.close();
                System.out.println(encoding.toString());
            } catch (Exception e) {
                System.err.println("Failed to read the file");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Failed to read the file");
        }
        return encoding;
    }

    // TODO
    public void cfg_2_tree() {
    }

    /**
     * Decompress the POPPT into text, and store to the file
     *
     * @param encoding POPPT encoding
     * @param file     target file
     */
    public void poppt_2_txt(LinkedList<String> encoding, String file) {
        // Restore the bit stream
        LinkedList<Byte> bit_stream = new LinkedList<>();
        for (String s : encoding.removeFirst().split("")) {
            bit_stream.add(Byte.parseByte(s));
        }
        int c = 0; // counter for bits '0' and '1'
        long i = 0; // counter for nonterminals
        Map<String, Pair<String, String>> Dict = new HashMap<String, Pair<String, String>>();
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
                                stack1.add(rhs.first);
                                stack1.add(rhs.second);
                            } else { // append to the text for a terminal
                                original_text += current_node;
                                outputWriter.print(current_node);
                            }
                        }
                    }
                    // System.out.println("original text so far: " + original_text);
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
                // Seems useless
                // if (c == 0) { // finished
                // System.out.println("Original text recovered: \n" + original_text);
                // String A = S.pop();
                // for (Map.Entry<String, Pair<String, String>> rule : Dict.entrySet()) {
                // System.out.println(rule.getKey() + "->" + rule.getValue().first + " " +
                // rule.getValue().second);
                // }
                // }
            }
            outputWriter.close();
        } catch (Exception e) {
            System.err.println("Failed to save the output");
        }
    }

    public static void main(String[] args) {
        Folca f = new Folca();
        String file;
        while (true) {
            // Display options
            System.out.println("1 - text to succinct grammar");
            System.out.println("2 - text to cfg");
            System.out.println("3 - cfg to succinct grammar");
            System.out.println("4 - cfg to grammar tree");
            System.out.println("5 - succinct grammar to text");
            System.out.println("6 - cfg to text");
            System.out.println("q - Exit");
            String inputs = Main.inputScanner.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given\n");
                break;
            }
            if (inputs.charAt(0) == 'q') {
                System.out.println("Exiting...\n");
                break;
            }
            switch (inputs.charAt(0)) {
                case '1': { // text -> cfg -> enc
                    new Folca().folca(false);
                    break;
                }
                case '2': { // text -> cfg
                    new Folca().folca(true);
                    break;
                }
                case '3': {
                    // cfg -> enc
                    System.out.println("Choose the file to convert: ");
                    file = Main.inputScanner.nextLine();
                    // Check if file exists
                    if (!new File(file).isFile()) {
                        System.out.println("Error: File does not exist.");
                        break;
                    }
                    // Check file format
                    if (!file.substring(file.length() - 4, file.length()).equals(".cfg")) {
                        System.out.println("Wrong file type, please select a file of type .cfg");
                    } else {
                        // Get input cfg
                        HashMap<String, Pair<String, String>> cfg = new ParseCFG(file).getCFG();
                        // Get the file name
                        file = file.substring(0, file.length() - 4);
                        // Encode cfg
                        CFG_2_POPPT cfg2poppt = new CFG_2_POPPT();
                        cfg2poppt.cfg2poppt(cfg, file);
                        break;
                    }
                }
                case '4': {
                    // cfg -> tree
                    break;
                }
                case '5': {
                    // Get input file
                    System.out.println("Choose the file to convert: ");
                    file = Main.inputScanner.nextLine();
                    // Check if file exists
                    if (!new File(file).isFile()) {
                        System.out.println("Error: File does not exist.");
                        break;
                    }
                    // Check file format
                    if (!file.substring(file.length() - 4, file.length()).equals(".slp")) {
                        System.out.println("Wrong file type, please select a file of type .slp");
                    } else {
                        // poppt -> text
                        f.poppt_2_txt(f.parsePOPPT(file), file.substring(0, file.length() - 4));
                    }
                    break;
                }
                case '6': {

                    // Get input file
                    System.out.println("Choose the file to convert: ");
                    file = Main.inputScanner.nextLine();
                    // Check if file exists
                    if (!new File(file).isFile()) {
                        System.out.println("Error: File does not exist.");
                        break;
                    }
                    // Check file format
                    if (!file.subSequence(file.length() - 4, file.length()).equals(".cfg")) {
                        System.out.println("Wrong file type, please select a file of type .cfg");
                    } else {
                        // cfg -> poppt -> text
                        CFG_2_POPPT cfg2poppt = new CFG_2_POPPT();
                        f.poppt_2_txt(cfg2poppt.cfg2poppt(new ParseCFG(file).getCFG()),
                                file.substring(0, file.length() - 4) + "(1)");
                    }
                    break;
                }
                default: {
                    System.out.println("Please enter a valid option.\n");
                    break;
                }
            }
        }
    }
}
