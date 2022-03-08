package slp.util;

import java.util.LinkedList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.io.ObjectInputStream;

/**
 * Parse the input file into POPPT.
 * 
 * @author Tianlong Zhong
 */
public class ParsePOPPT {
    public LinkedList<String> encoding;

    public ParsePOPPT() {
        encoding = new LinkedList<String>();
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
                // System.out.println(encoding.toString());
            } catch (Exception e) {
                System.out.println("Failed to read the file");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Failed to read the file");
        }
        return encoding;
    }
}
