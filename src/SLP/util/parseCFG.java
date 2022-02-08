package SLP.util;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import SLP.Pair;

public class ParseCFG {
    String path;
    HashMap<String, Pair<String, String>> cfg;

    public ParseCFG(String path) {
        cfg = new HashMap<String, Pair<String, String>>();
        this.path = path;
    }

    /**
     * 
     * @return the parsed context-free grammar of SLP
     */
    public HashMap<String, Pair<String, String>> getCFG() {
        // Read the file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = br.readLine()) != null) {
                int counter = 0;
                String lhs = "";
                Pair<String, String> rhs = new Pair<String, String>();
                rhs.first = "";
                rhs.second = "";
                // Parse LHS
                while (counter < line.length()) {
                    if (line.charAt(counter) != '-') {
                        lhs += line.charAt(counter);
                        counter++;
                    } else {
                        counter += 2; // consume '->'
                        break;
                    }
                }
                // Parse RHS
                if (counter == line.length()) { // rhs.first is newline
                    rhs.first = "\n";
                    line = br.readLine(); // read the next line
                    int c = 1; // consume the space
                    // Parse rhs.second
                    if (c == line.length()) { // rhs.second is newline
                        rhs.second = "\n";
                        br.readLine();
                    } else {
                        while (c < line.length()) {
                            rhs.second += line.charAt(c);
                            c++;
                        }
                    }
                } else if (line.charAt(counter) == ' ') { // rhs.first is space
                    counter++; // consume the space
                    // Parse rhs.second
                    if (counter == line.length()) { // rhs.second is newline
                        rhs.second = "\n";
                        br.readLine();
                    } else {
                        while (counter < line.length()) {
                            rhs.second += line.charAt(counter);
                            counter++;
                        }
                    }
                } else { // rhs.first is terminal/nonterminal
                    while (counter < line.length()) {
                        if (line.charAt(counter) != ' ') {
                            rhs.first += line.charAt(counter);
                            counter++;
                        } else {
                            counter++; // consume the space
                            break;
                        }
                    }
                    // Parse rhs.second
                    if (counter == line.length()) { // rhs.second is newline
                        rhs.second = "\n";
                        br.readLine();
                    } else {
                        while (counter < line.length()) {
                            rhs.second += line.charAt(counter);
                            counter++;
                        }
                    }
                }
                // Add production rule
                cfg.put(lhs, rhs);
            }
            flush();
        } catch (Exception e) {
            System.out.println("Failed to read the file");
            // e.printStackTrace();
        }
        return cfg;
    }

    public void flush() {
        for (HashMap.Entry<String, Pair<String, String>> rule : this.cfg.entrySet()) {
            System.out.println(rule.getKey() + "->" + rule.getValue().first + " " +
                    rule.getValue().second);
        }
    }
}
