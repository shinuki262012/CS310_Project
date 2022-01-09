import java.util.*;

import javax.swing.plaf.synth.SynthSpinnerUI;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Fully Online LCA implementation based on S. Maruyama and Y. Tabei,
 * "Fully Online Grammar Compression in Constant Space," 2014 Data Compression
 * Conference, 2014, pp. 173-182, doi: 10.1109/DCC.2014.69.
 * 
 * 
 */

public class FOLCA {
    public static Scanner input;
    public static ArrayList<Queue<String>> queues = new ArrayList<>(); // queues used for processing symbols
    public static Map<String, Pair<String, String>> D = new HashMap<String, Pair<String, String>>(); // phrase
                                                                                                     // dictionary
    public static Map<String, Integer> freq_counter = new HashMap<String, Integer>(); // frequency counter
    public static int k = 1024; // max size of the pharse dictionary
    public static double ep = 5; // vacancy rate

    public static Map<String, String> D_r = new HashMap<String, String>(); // reverse dictionary
    public static long nonTerminalCounter = 0; // counter for nontermials
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    /**
     * Fully Online LCA implementation
     */
    public static void folca() {
        // Initialize queue with 2 dummy symbols
        Queue<String> q0 = new LinkedList<String>();
        q0.add("");
        q0.add("");
        queues.add(q0);
        Scanner input = new Scanner(System.in);
        System.out.println("Choose the file to convert: ");
        String file = input.nextLine();
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
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Failed to read the file");
        }
        // DELETE
        for (Map.Entry<String, Pair<String, String>> rule : D.entrySet()) {
            System.out.println(rule.getKey() + "->" + rule.getValue().first + " " +
                    rule.getValue().second);
        }
        for (Map.Entry<String, String> rule : D_r.entrySet()) {
            System.out.println(rule.getKey() + "<-" + rule.getValue());
        }

        // Encode the resulting CFG into a succinct form POPPT

        // Get the starting symbol: the root node in the parse tree
        queues.get(queues.size() - 1).poll();
        queues.get(queues.size() - 1).poll();
        String start_symbol = queues.get(queues.size() - 1).poll();
        System.out.println("Start symbol: " + start_symbol);

        // Build a POPPT out of the POSLP
        Queue<Byte> bit_stream = new LinkedList<>();
        Queue<String> leaves = new LinkedList<>();
        ArrayList<String> inners = new ArrayList<>();
        ArrayList<Boolean> inner_appeared_twice = new ArrayList<>();
        // Set of non terminals, each rule is to be applied once
        Set<String> key_set = D.keySet();
        System.out.println(key_set);

        // 2 stacks used for the post order traversal
        Stack<String> stack1 = new Stack<String>();
        Stack<String> stack2 = new Stack<String>();
        stack1.add(start_symbol);
        while (!stack1.isEmpty()) {
            String current_node = stack1.peek();
            if (D.containsKey(current_node)) {
                inners.add(current_node);
                inner_appeared_twice.add(false);
                Pair<String, String> rhs = D.get(current_node);
                stack1.add(rhs.second);
                stack1.add(rhs.first);
                // Remove the applied rule
                D.remove(current_node);

            } else {
                stack1.pop();
                stack2.add(current_node);
                if (inners.contains(current_node) && !inner_appeared_twice.get(inners.indexOf(current_node))) {
                    inner_appeared_twice.set(inners.indexOf(current_node), true);
                    bit_stream.add((byte) 1);
                } else {
                    leaves.add(current_node);
                    bit_stream.add((byte) 0);
                }
            }
        }
        bit_stream.add((byte) 1);// add an extra virtual node
        System.out.println(bit_stream);
        System.out.println(stack2);
        System.out.println(inners);
        System.out.println(leaves);
        poppt_2_txt(bit_stream, leaves);
    }

    /**
     * Process the given character X inserted into the given level
     * 
     * @param level the level where X is inserted into
     * @param X     input character X
     */
    public static void processSymbol(int level, String X) {
        System.out.println("Processing " + X);
        Queue<String> q_k = queues.get(level);
        q_k.add(X);
        System.out.println(queues.toString());
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
     * @param q_k substring of length 4: q_k[pos-1, pos, pos+1, pos+2]
     * @param pos position
     * @return whether position pos in q_k is a landmark
     */
    public static boolean landmark(Queue<String> q_k, int pos) {
        System.out.println("landmark for" + q_k.toString());
        // Copy the given queue into an array
        String[] qk = new String[q_k.size()];
        for (int j = 0; j < q_k.size(); j++) {
            qk[j] = (String) q_k.remove();
            q_k.add(qk[j]);
        }

        if (qk[0] == "") { // the queue starts with dummy symbols, qk[pos, pos+1] should not be paired
            System.out.println("0");
            return false;
        } else {
            if (qk[pos + 1].equals(qk[pos + 2])) { // qk[pos+1, pos+2] is a repetitive pair
                System.out.println("1");
                return false;
            } else if (isMinimal(qk, pos) || isMaximal(qk, pos)) { // qk[pos, pos+1] is a minimal/maximal pair
                System.out.println("3");
                return true;
            } else { // qk[pos, pos+2] contains no special pair
                System.out.println("5");
                return true;
            }

        }
    }

    /**
     * queue = {i-1, i, i+1, i+2}, check if queue[i, i+1] is a minimal pair
     * queue[i, i+1] is a minimal pair if queue[i+1] < queue[i], queue[i+2]
     * 
     * @param queue given queue of length 4
     * @param pos   given position
     * @return whether queue[i, i+1] is a minimal pair
     */
    public static boolean isMinimal(String[] queue, int pos) {
        // [?, terminal, ?]
        if (queue[pos].length() == 1) {
            // [terminal, terminal, ?]
            if (queue[pos - 1].length() == 1) {
                if (queue[pos + 1].length() == 1) { // [terminal, terminal, terminal]
                    return ((((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0)))
                            && (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0)))) ? true : false;
                } else { // [terminal, terminal, nonterminal]
                    return (((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0))) ? true : false;
                }
            }
            // [nonterminal, terminal, terminal]
            else if (queue[pos + 1].length() == 1) {
                return (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0))) ? true : false;
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
                return (queue[pos].compareTo(queue[pos - 1]) < 0 && queue[pos].compareTo(queue[pos + 1]) < 0) ? true
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
     * @param queue given queue of length 4
     * @param pos   given position
     * @return whether queue[i, i+1] is a maximal pair
     */
    public static boolean isMaximal(String[] queue, int pos) {
        System.out.println("is maximal ? " + pos);
        String s0 = queue[pos - 1];
        String s1 = queue[pos];
        String s2 = queue[pos + 1];
        String s3 = queue[pos + 2];
        // Check if the queue is in increasing/decreasing order
        if ((s0.compareTo(s1) < 0 && s1.compareTo(s2) < 0 && s2.compareTo(s3) < 0)
                || (s0.compareTo(s1) > 0 && s1.compareTo(s2) > 0 && s2.compareTo(s3) > 0)) {

            return (((lca(s1, s2)) > lca(s0, s1)) && ((lca(s1, s2)) > lca(s2, s3))) ? true : false;
        } else
            return false;
    }

    /**
     * Calculate the least common ancestor of the input pair of strings.
     * 
     * @param i character
     * @param j character
     * @return least common ancestor of (i, j) in the complete binary tree of the
     *         alphabets
     */
    public static long lca(String i, String j) {
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
     * @param X first symbol in pair
     * @param Y second symbol in pair
     * @return the nonterminal that produces the pair
     */
    public static String update(String X, String Y) {
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
     * @param X first symbol in pair
     * @param Y second symbol in pair
     * @return the nonterminal that produces the pair
     */
    public static String freqCountingUpdate(String X, String Y) {
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
                        System.out.println(nonterminal);
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
    public static long nonterminal_2_long(String nonterminal) {
        String alphabet = String.valueOf(nonterminal.charAt(0));
        int a = (int) Integer.valueOf(Arrays.asList(alphabets).indexOf(alphabet));
        System.out.println(a);
        long n = (long) Integer.valueOf(nonterminal.substring(1));
        System.out.println(n);
        return n * 26 + a;
    }

    /**
     * Get the nonterminal represented by the given integer
     * 
     * @param x an integer
     * @return the nontermial representing the integer
     */
    public static String long_2_nonterminal(long x) {
        int n = (int) x / 26;
        return alphabets[(int) x % 26] + String.valueOf(n);
    }

    /**
     * Restruct the text from the POPPT
     * 
     * @param bit_stream bit string representing the POPPT
     * @param leaves     label sequences representing the leaves of the POPPT
     */
    public static void poppt_2_txt(Queue<Byte> bit_stream, Queue<String> leaves) {
        int c = 0; // counter for bits '0' and '1'
        long i = 0; // counter for nonterminals
        Map<String, Pair<String, String>> Dict = new HashMap<String, Pair<String, String>>();
        Stack<String> S = new Stack<String>();
        String original_text = "";
        while (!bit_stream.isEmpty()) {
            byte bit = bit_stream.poll();
            if (bit == 0) { // leaf node
                c++;
                String leaf = leaves.poll();
                S.add(leaf);
                if (leaf.length() == 1) { // terminals
                    original_text += leaf;
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
                        }
                    }
                }
                System.out.println("original text so far: " + original_text);
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

    }

    /**
     * Encode the CFG into POPPT
     */
    public static void cfg_2_poppt() {

    }

    public static void cfg_2_tree() {

    }

    public static void main(String[] args) {
        while (true) {
            // Display options
            System.out.println("1 - Compress (text to succinct grammar)");
            System.out.println("2 - Visualise (complete grammar)");
            System.out.println("3 - Visualise (grammar tree)"); // complete or succinct?
            System.out.println("4 - Decompress (succinct grammar to text)");
            System.out.println("q - Exit");
            Scanner input = new Scanner(System.in);
            String inputs = input.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given\n");
                break;
            }
            switch (inputs.charAt(0)) {
                case '1':
                    folca();
                    break;
                case '2':
                    break;
                case '3':
                    break;
                case 'q':
                    break;
                default:
                    System.out.println("Please enter a valid option.\n");
                    break;
            }
            break;
        }

    }
}
