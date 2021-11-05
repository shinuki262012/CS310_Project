import java.util.*;
import java.io.*;

public class Factorization {

    /**
     * LZ77 Factorization
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Get input
        // String input = "banana";
        String input = "zzzzzipzip";
        int[] array = new int[input.length() + 3];
        for (int i = 0; i < input.length(); i++) {
            array[i] = (int) input.charAt(i);
            System.out.println(array[i]);
        }
        // Compute the suffix array of the string input
        Skew SA = new Skew();
        int[] suffixArray = SA.buildSuffixArray(array, 0, input.length());
        for (int i = 0; i < suffixArray.length; i++) {
            System.out.println(Integer.toString(suffixArray[i]));
        }

        // Trim the suffix array
        int[] suffix_array = Arrays.copyOfRange(suffixArray, 0, input.length());
        // CallKKP2, return the factorization
        ArrayList<Pair<Integer, Integer>> F = new ArrayList<Pair<Integer, Integer>>();
        byte[] X = input.getBytes();
        int result = kkp2(X, suffix_array, input.length(), F);
        System.out.println(result);

    }

    public static int parse_phrase(byte[] X, int n, int i, int psv, int nsv, ArrayList<Pair<Integer, Integer>> F) {
        int pos;
        int len = 0;
        if (nsv == -1) {
            while (X[psv + len] == X[i + len]) {
                ++len;
            }
            pos = psv;
        } else if (psv == -1) {
            while (i + len < n && X[nsv + len] == X[i + len]) {
                ++len;
            }
            pos = nsv;
        } else {
            while (X[psv + len] == X[nsv + len]) {
                ++len;
            }
            if (X[i + len] == X[psv + len]) {
                ++len;
                while (X[i + len] == X[psv + len]) {
                    ++len;
                }
                pos = psv;
            } else {
                while (i + len < n && X[i + len] == X[nsv + len]) {
                    ++len;
                }
                pos = nsv;
            }
        }
        if (len == 0) {
            pos = X[i];
        }
        if (F != null) {
            F.add(new Pair<Integer, Integer>(pos, len));
        }
        return i + Math.max(1, len);
    }

    /**
     * kkp2 - A LZ77 impelementation converted form the C++ implementation //
     * Copyright (c) 2013 Juha Karkkainen, Dominik Kempa and Simon J. Puglisi
     * 
     * @param X  input string
     * @param SA suffix array
     * @param n  length of input string
     * @param F  holds the resulting factorization
     * @return
     */
    public static int kkp2(byte[] X, int[] SA, int n, ArrayList<Pair<Integer, Integer>> F) {
        for (int i = 0; i < X.length; i++) {
            System.out.println(X[i]);
        }
        for (int i = 0; i < SA.length; i++) {
            System.out.println(SA[i]);
        }
        System.out.println(n);

        if (n == 0)
            return 0;

        int[] CS = new int[n + 5];
        int[] stack = new int[(1 << 16) + 5];
        int top = 0;
        stack[top] = 0;

        // Compute PSV_text for SA and save into CS.
        CS[0] = -1;
        for (int i = 1; i <= n; ++i) {
            int sai = SA[i - 1] + 1;
            while (stack[top] > sai) {
                --top;
            }
            if ((top & (((1 << 16)) - 1)) == 0) {
                if (stack[top] < 0) {
                    // Stack empty -- use implicit.
                    top = -stack[top];
                    while (top > sai) {
                        top = CS[top];
                    }
                    stack[0] = -CS[top];
                    stack[1] = top;
                    top = 1;
                } else if (top == (1 << 16)) {
                    // Stack is full -- discard half.
                    for (int j = (1 << (16 - 1)); j <= (1 << 16); ++j) {
                        stack[j - (1 << (16 - 1))] = stack[j];
                    }
                    stack[0] = -stack[0];
                    top = (1 << (16 - 1));
                }
            }

            int addr = sai;
            CS[addr] = Math.max(0, stack[top]);
            ++top;
            stack[top] = sai;
        }
        stack = null;

        // Compute the phrases.
        CS[0] = 0;
        int nfactors = 0;
        int next = 1;
        int nsv;
        int psv;
        for (int t = 1; t <= n; ++t) {
            psv = CS[t];
            nsv = CS[psv];
            if (t == next) {
                next = parse_phrase(X, n, t - 1, psv - 1, nsv - 1, F) + 1;
                ++nfactors;
            }
            CS[t] = nsv;
            CS[psv] = t;
        }

        // Clean up.
        CS = null;
        return nfactors;
    }
}