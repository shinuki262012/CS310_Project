import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FOLCA {
    public static Scanner input;
    // queues: q_k: queue at level k
    public static ArrayList<Queue<String>> queues = new ArrayList<>();
    // D: phrase dictionary
    public static Map<String, Pair<String, String>> D = new HashMap<String, Pair<String, String>>();
    // Frequency counter
    public static Map<String, Integer> freq_counter = new HashMap<String, Integer>();
    public static int k = 1024; // max size of the pharse dictionary
    public static double ep = 5; // vacancy rate
    // D_r: reverse dictionary
    public static Map<String, String> D_r = new HashMap<String, String>();
    // A list of nonterminals
    public static Queue<String> non_terminals = new LinkedList<>();
    public static String[] alphabets = { "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    /**
     * Fully online LCA implementation
     */
    public static void Folca() {
        // Initialize queues
        Queue<String> q0 = new LinkedList<String>(); // queue is initialized with 2 dummy symbols
        q0.add("");
        q0.add("");
        queues.add(q0);
        // Initialize nonterminals
        non_terminals = populate_nonterminals("");
        Scanner input = new Scanner(System.in);
        System.out.println("Choose the file to convert: ");
        String file = input.nextLine();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            int c;
            try {
                while ((c = reader.read()) != -1) {
                    String character = String.valueOf((char) c);
                    ProcessSymbol(0, character);
                }
                // Finish up
                boolean finish = false;
                while (!finish) {
                    finish = true;
                    for (int i = 0; i < queues.size() - 1; i++) {
                        if (queues.get(i).size() == 3) {
                            queues.get(i).poll();
                            queues.get(i).poll();
                            ProcessSymbol(i + 1, queues.get(i).poll());
                            finish = false;
                        } else if (queues.get(i).size() == 4) {
                            queues.get(i).poll();
                            queues.get(i).poll();
                            String qk3 = queues.get(i).poll();
                            String qk4 = queues.get(i).poll();
                            queues.get(i).add(qk3);
                            queues.get(i).add(qk4);
                            // String Y = Update(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                            String Y = Freq_counting(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                            ProcessSymbol(i + 1, Y); // add the nonterminal Y to upper tree level
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
        for (Map.Entry<String, Pair<String, String>> rule : D.entrySet()) {
            System.out.println(rule.getKey() + "->" + rule.getValue().first + " " +
                    rule.getValue().second);
        }
        for (Map.Entry<String, String> rule : D_r.entrySet()) {
            System.out.println(rule.getKey() + "<-" + rule.getValue());
        }

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
    }

    /**
     * Process the input symbol X
     * 
     * @param level the level where X is added into
     * @param X     next input character X
     */
    public static void ProcessSymbol(int level, String X) {
        System.out.println("Processing " + X);
        Queue<String> q_k = queues.get(level);
        q_k.add(X);
        System.out.println(queues.toString());
        if (q_k.size() == 4) { // build a 2-tree
            if (!Landmark(q_k, 1)) {
                if (level + 1 == queues.size()) { // The next level queue is not defined yet
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
                // String Y = Update(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                String Y = Freq_counting(qk3, qk4); // replace q_k[3], q_k[4] with nonterminal Y
                ProcessSymbol(level + 1, Y); // add the nonterminal Y to upper tree level
            }

        } else if (q_k.size() == 5) {
            if (level + 1 == queues.size()) { // The next level queue is not defined yet
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
            // String Y = Update(qk4, qk5); // replace q_k[4], q_k[5] with nonterminal Y
            // String Z = Update(qk3, Y); // replace q_k[3], Y with nonterminal Z
            String Y = Freq_counting(qk4, qk5); // replace q_k[4], q_k[5] with nonterminal Y
            String Z = Freq_counting(qk3, Y); // replace q_k[3], Y with nonterminal Z
            ProcessSymbol(level + 1, Z); // add nonterminal Z to upper tree level
        }

    }

    // TODO: isMaximal
    /**
     * 
     * @param q_k substrings of length 4
     * @param i   position
     * @return Whether the i-th position in q_k is a landmark
     */
    public static boolean Landmark(Queue<String> q_k, int i) {
        System.out.println("Landmark for" + q_k.toString());
        String[] qk = new String[q_k.size()];
        for (int j = 0; j < q_k.size(); j++) {
            qk[j] = (String) q_k.remove();
            q_k.add(qk[j]);
        }
        if (qk[0] == "") {
            System.out.println("0");
            return false;
            // return (!qk[i + 1].equals(qk[i + 2]));
        } else {
            if (qk[i + 1].equals(qk[i + 2])) {
                System.out.println("1");
                return false;
            }
            // else if (isMinimal(qk, i) || isMaximal(qk, i)) { // qk[i, i+1] is a
            // minimal/maximal pair
            // System.out.println("3");
            // return true;
            // }
            else if (isMinimal(qk, i + 1) || isMaximal(qk, i + 1)) {// qk[i+1, i+2] is a minimal/maximal pair
                System.out.println("4");
                return false;
            } else {
                System.out.println("5");
                return true;
            }

        }
    }

    /**
     * queue = [i-1, i, i+1, i+2], check if (i, i+1) is a minimal pair
     * 
     * @param queue
     * @param pos
     * @return
     */
    public static boolean isMinimal(String[] queue, int pos) {
        if (queue[pos].length() == 1) {
            if (queue[pos - 1].length() == 1) {
                if (queue[pos + 1].length() == 1) {
                    if ((((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0)))
                            && (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0)))) {
                        return true;
                    }
                } else if (((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0))) {
                    return true;
                }
            } else if (queue[pos + 1].length() == 1) {
                if (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0))) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isMaximal(String[] queue, int pos) {
        if (queue[pos].length() == 1) {
            if (queue[pos - 1].length() == 1) {
                if (queue[pos + 1].length() == 1) {
                    if ((((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0)))
                            && (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0)))) {
                        return true;
                    }
                } else if (((int) queue[pos].charAt(0)) < ((int) queue[pos - 1].charAt(0))) {
                    return true;
                }
            } else if (queue[pos + 1].length() == 1) {
                if (((int) queue[pos].charAt(0)) < ((int) queue[pos + 1].charAt(0))) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param X first symbol in digram
     * @param Y second symbol in digram
     * @return the nonterminal that produces the digram
     */
    public static String Update(String X, String Y) {
        Pair<String, String> rhs = new Pair<>(X, Y);
        String Z = D_r.get(X + Y);
        if (Z == null) { // check if the rhs is in the reverse dictionary
            Z = get_nonterminal(); // assign a new nonterminal
            D_r.put(X + Y, Z); // add the new production rule to the reverse dictionary
            if (!D.containsKey(Z)) {
                D.put(Z, rhs); // add the new production rule to the phrase dictionary
            }
        }
        return Z;
    }

    public static String Freq_counting(String X, String Y) {
        String Z = D_r.get(X + Y);
        if (Z != null) { // D contains the rule
            if (freq_counter.containsKey(Z)) {
                freq_counter.replace(Z, freq_counter.get(Z) + 1);
            } else {
                freq_counter.put(Z, 1);
            }
        } else { // D does not contains the rule
            Z = get_nonterminal();
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
                            // D.remove(nonterminal);
                            iterator.remove();
                            freq_counter.remove(nonterminal);
                        }
                    }
                }
            }
            // Add new production rules
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
     * Get a nonterminal
     * 
     * @return a nonterminal
     */
    public static String get_nonterminal() {
        if (non_terminals.size() == 1) {
            // create more nonterminals
            String last_nonterminal = non_terminals.remove();
            non_terminals = populate_nonterminals(last_nonterminal);
            return last_nonterminal;
        }
        return non_terminals.remove();
    }

    /**
     * Create more nonterminals
     * 
     * @param last_nonterminal the last nonterminal
     * @return the queue of nonterminals created
     */
    public static Queue<String> populate_nonterminals(String last_nonterminal) {
        if (last_nonterminal == "") {
            non_terminals = new LinkedList<>();
            // Initially create 128 nonterminals
            for (int i = 0; i < 128; i++) {
                int n = (int) i / 26;
                non_terminals.add(alphabets[i % 26] + String.valueOf(n));
            }
        } else {
            // Get the number of nonterminals created so far from the last nonterminal
            String alphabet = String.valueOf(last_nonterminal.charAt(0));
            int a = (int) Integer.valueOf(Arrays.asList(alphabets).indexOf(alphabet));
            System.out.println(a);
            long n = (long) Integer.valueOf(last_nonterminal.substring(1));
            System.out.println(n);
            long next = n * 26 + a + 1;
            System.out.println(next);
            // Add double number of terminals
            for (long i = next; i < next * 2; i++) {
                int x = (int) i / 26;
                non_terminals.add(alphabets[(int) i % 26] + String.valueOf(x));
            }
        }
        return non_terminals;
    }

    public static void decompress(Queue<Byte> bit_stream, Queue<String> leaves) {
        int c = 0;
        long i = 0;
        Map<String, Pair<String, String>> D = new HashMap<String, Pair<String, String>>();
        Stack<String> S = new Stack<String>();
        while (!bit_stream.isEmpty()) {
            byte bit = bit_stream.poll();
            if (bit == 0) {
                c++;
                S.add(leaves.poll());
            } else {
                c--;
                String lhs = S.pop();
                String rhs = S.pop();
                Pair<String, String> rule = new Pair<String, String>(lhs, rhs);
                String x_i = "X" + String.valueOf(i);
                D.put(x_i, rule);
                i++;
                S.add(x_i);
            }
            if (c == 0) { //
                String A = S.pop();
                Pair<String, String> rule = new Pair<String, String>();
                // Recover subtext using A and D

            }

        }

    }

    public static void main(String[] args) {
        Folca();
    }
}
