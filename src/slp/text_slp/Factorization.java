package slp;

import java.util.*;

import slp.util.Pair;

import java.io.*;

public class Factorization {

    /**
     * LZ77 Factorization
     * 
     * @param args
     */
    public static ArrayList<Pair<Integer, Integer>> factorization(String input) {
        // Get input
        // System.out.println("Input: " + input);
        int[] array = new int[input.length() + 3];
        for (int i = 0; i < input.length(); i++) {
            array[i] = (int) input.charAt(i);
            // System.out.println(array[i]);
        }
        // Compute the suffix array of the string input
        Skew SA = new Skew();
        int[] suffixArray = SA.buildSuffixArray(array, 0, input.length());

        // Trim the suffix array
        int[] suffix_array = Arrays.copyOfRange(suffixArray, 0, input.length());
        // Print the suffix array
        // System.out.print("Suffix array: [" + Integer.toString(suffix_array[0]));
        // for (int i = 1; i < suffix_array.length; i++) {
        // System.out.print(", " + Integer.toString(suffix_array[i]));
        // }
        // System.out.print("]\n");
        // CallKKP2, return the factorization
        ArrayList<Pair<Integer, Integer>> F = new ArrayList<Pair<Integer, Integer>>();
        byte[] X = input.getBytes();
        int result = kkp2(X, suffix_array, input.length(), F); // number of factors
        // System.out.println(result);
        // Print the factorization
        // System.out.println("LZ77 factorization: ");
        // for (int i = 0; i < F.size(); i++) {
        // int first = F.get(i).first;
        // int second = F.get(i).second;
        // if (second == 0) { // if first can be converted to a ASCII char
        // char letter = (char) first;
        // System.out.print("(" + letter + ", " + F.get(i).second + ")");
        // } else {
        // System.out.print("(" + first + ", " + F.get(i).second + ")");
        // }
        // }
        // System.out.println("");

        return F;

    }

    public static int parse_phrase(byte[] X, int n, int i, int psv, int nsv, ArrayList<Pair<Integer, Integer>> F) {
        int pos, len = 0;
        if (nsv == -1) {
            if (psv < 0) {
                while (0 == X[i + len]) {
                    ++len;
                }
            } else {

                while (X[psv + len] == X[i + len]) {
                    ++len;
                }
            }
            pos = psv;
        } else if (psv == -1) {
            while ((i + len < n) && (X[nsv + len] == X[i + len])) {
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

        // for (int i = 0; i < X.length; i++) {
        // System.out.println(X[i]);
        // }
        // for (int i = 0; i < SA.length; i++) {
        // System.out.println(SA[i]);
        // }
        // System.out.println(n);

        int stack_bits = 16;
        int stack_size = 1 << stack_bits;
        int stack_half = 1 << (stack_bits - 1);
        int stack_mask = stack_size - 1;

        if (n == 0)
            return 0;

        int[] CS = new int[n + 5];
        int[] stack = new int[stack_size + 5];
        int top = 0;
        stack[top] = 0;

        // Compute PSV_text for SA and save into CS.
        CS[0] = -1;
        for (int i = 1; i <= n; ++i) {
            int sai = SA[i - 1] + 1;
            // System.out.println("i: " + i + "SA[i-1]:" + SA[i - 1] + "sai:" + sai);
            while (stack[top] > sai) {
                --top;
            }
            if ((top & stack_mask) == 0) {
                if (stack[top] < 0) {
                    // Stack empty -- use implicit.
                    top = -stack[top];
                    while (top > sai) {
                        top = CS[top];
                    }
                    stack[0] = -CS[top];
                    stack[1] = top;
                    top = 1;
                } else if (top == stack_size) {
                    // Stack is full -- discard half.
                    for (int j = stack_half; j <= stack_size; ++j) {
                        stack[j - stack_half] = stack[j];
                    }
                    stack[0] = -stack[0];
                    top = stack_half;
                }
            }

            int addr = sai;

            // System.out.println("top: " + top + " stack[top]:" + stack[top]);
            CS[addr] = Math.max(0, stack[top]);
            ++top;
            stack[top] = sai;
        }
        // Compute the phrases.
        CS[0] = 0;
        int nfactors = 0;
        int next = 1;
        int nsv, psv;
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

        return nfactors;
    }

    public static void main(String[] args) {
        // String input = "banana";
        String input = "zzzzzipzip";

        factorization(input);
    }
}