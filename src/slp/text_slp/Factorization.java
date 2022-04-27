package slp.text_slp;

import slp.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2013 Juha Karkkainen, Dominik Kempa and Simon J. Puglisi
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following
// conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.
////////////////////////////////////////////////////////////////////////////////

/**
 * Linear time algorithm computing the LZ77 factorization translated from the
 * C++ implementation (https://github.com/martiniani-lab/sweetsourcod.git)
 * based on the paper:
 * 
 * Juha Karkkainen, Dominik Kempa and Simon J. Puglisi,
 * Linear Time Lempel-Ziv Factorization: Simple, Fast, Small.
 * In Proc. CPM 2013, LNCS vol. 7922, pp. 189-200. Springer 2013.
 * 
 * Original License attach above.
 */
public class Factorization {
    public ArrayList<Pair<Integer, Integer>> lz77Factorization = new ArrayList<Pair<Integer, Integer>>();
    public byte[] inputString;

    /**
     * @param input input string
     * @return lz77 factorization of the input string
     */
    public ArrayList<Pair<Integer, Integer>> factorization(String input) {
        // Convert input string to a list of integers
        int[] array = new int[input.length() + 3];
        for (int i = 0; i < input.length(); i++) {
            array[i] = (int) input.charAt(i);
        }

        // Compute the suffix array of the input
        Skew sa = new Skew();
        int[] suffixArray = sa.buildSuffixArray(array, 0, input.length());

        // Trim the suffix array
        int[] suffix_array = Arrays.copyOfRange(suffixArray, 0, input.length());

        // Compute the lz77 factorization
        inputString = input.getBytes();
        kkp2(suffix_array, input.length());
        return lz77Factorization;
    }

    /**
     * 
     * @param n   length of input string
     * @param i   starting position of the pharse
     * @param psv previous smaller values
     * @param nsv next smaller values
     * @return the starting position of the next phrase
     */
    public int parse_phrase(int n, int i, int psv, int nsv) {
        int pos, len = 0;
        if (nsv == -1) {
            if (psv < 0) {
                while (0 == inputString[i + len]) {
                    ++len;
                }
            } else {
                while (inputString[psv + len] == inputString[i + len]) {
                    ++len;
                }
            }
            pos = psv;
        } else if (psv == -1) {
            while ((i + len < n) && (inputString[nsv + len] == inputString[i + len])) {
                ++len;
            }
            pos = nsv;
        } else {
            while (inputString[psv + len] == inputString[nsv + len]) {
                ++len;
            }
            if (inputString[i + len] == inputString[psv + len]) {
                ++len;
                while (inputString[i + len] == inputString[psv + len]) {
                    ++len;
                }
                pos = psv;
            } else {
                while (i + len < n && inputString[i + len] == inputString[nsv + len]) {
                    ++len;
                }
                pos = nsv;
            }
        }
        if (len == 0) {
            pos = inputString[i];
        }
        if (lz77Factorization != null) {
            lz77Factorization.add(new Pair<Integer, Integer>(pos, len));
        }
        return i + Math.max(1, len);
    }

    /**
     * kkp2 - A LZ77 impelementation converted form the C++ implementation
     * Copyright (c) 2013 Juha Karkkainen, Dominik Kempa and Simon J. Puglisi
     * 
     * @param sa suffix array
     * @param n  length of input string
     * @return number of factors
     */
    public int kkp2(int[] sa, int n) {
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

        // Compute PSV_text for sa and save into CS.
        CS[0] = -1;
        for (int i = 1; i <= n; ++i) {
            int sai = sa[i - 1] + 1;
            // System.out.println("i: " + i + "sa[i-1]:" + sa[i - 1] + "sai:" + sai);
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
                next = parse_phrase(n, t - 1, psv - 1, nsv - 1) + 1;
                ++nfactors;
            }
            CS[t] = nsv;
            CS[psv] = t;
        }

        return nfactors;
    }

}