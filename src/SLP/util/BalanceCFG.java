package slp.util;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class BalanceCFG {
    HashMap<String, Pair<String, String>> cfg;
    HashMap<String, Pair<String, String>> balancedCFG;

    public BalanceCFG(HashMap<String, Pair<String, String>> cfg) {
        balancedCFG = new HashMap<String, Pair<String, String>>();
        this.cfg = cfg;
    }

    /**
     * 
     * @return balanced grammar
     */
    public HashMap<String, Pair<String, String>> balance() {

        return null;
    }
}
