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
        // Get SA from input
        // String input = "banana";
        String input = "zzzzzipzip";
        int[] array = new int[input.length() + 3];
        for (int i = 0; i < input.length(); i++) {
            array[i] = (int) input.charAt(i);
            System.out.println(array[i]);
        }
        Skew SA = new Skew();
        int[] suffixArray = SA.buildSuffixArray(array, 0, input.length());
        for (int i = 0; i < suffixArray.length; i++) {
            System.out.println(Integer.toString(suffixArray[i]));
            System.out.println(" ");
        }

        // CallKKP2, return the factorization
    }

}