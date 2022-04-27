package slp;

import slp.text_slp.*;
import slp.lz77_slp.*;
import slp.util.*;

import java.util.Scanner;

/**
 * @author Tianlong Zhong
 */
public class Main {

    public static Scanner inputScanner;
    static Text_2_SLP text2slp;
    static Folca folca;
    static LZ77_2_SLP lz772slp;
    static SLP_2_LZ77 slp2lz77;

    /**
     * Display the main menu
     * 
     * @param args
     */
    public static void main(String[] args) throws GeneralException {
        inputScanner = new Scanner(System.in);
        menu();
        inputScanner.close();
    }

    /**
     * Main menu
     */
    public static void menu() throws GeneralException {
        String inputs;
        while (true) {
            System.out.println("\nMenu: ");
            System.out.println("==================");
            System.out.println("1 - Text to SLP");
            System.out.println("2 - LZ77 to SLP");
            System.out.println("3 - SLP to LZ77");
            System.out.println("q - Exit");
            System.out.println("==================");
            System.out.println("Enter an option to start: ");

            inputs = inputScanner.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given.\n");
                break;
            }
            if (inputs.charAt(0) == 'q') {
                System.out.println("Exiting...\n");
                break;
            }
            switch (inputs.charAt(0)) {
                case '1':
                    System.out.println("Option 1 selected.\n");
                    text_2_slp_menu();
                    break;
                case '2':
                    System.out.println("Option 2 selected.\n");
                    lz77_2_slp_menu();
                    break;
                case '3':
                    System.out.println("Option 3 selected.\n");
                    slp_2_lz77_menu();
                    break;
                default:
                    System.out.println("Please enter a valid option.\n");
                    break;
            }
        }
    }

    /**
     * Text to SLP menu
     */
    public static void text_2_slp_menu() {
        System.out.println("Choose the online or offline version of the compression algorithm: ");
        System.out.println("1 - offline \n2 - online");
        String inputs = inputScanner.nextLine().toString().trim();
        if (inputs.isEmpty()) {
            System.out.println("No option was given.");
        } else {
            char option = inputs.charAt(0);
            if (option == '1') { // offline version
                text2slp = new Text_2_SLP();
                text2slp.main(null);
            } else if (option == '2') { // online version
                folca = new Folca();
                folca.main(null);
            } else {
                System.out.println("Please enter a valid option");
            }
        }
    }

    /**
     * LZ77 to SLP menu
     */
    public static void lz77_2_slp_menu() {
        System.out.println("Choose the file to convert: ");
        String inputs = inputScanner.nextLine().toString().trim();
        if (inputs.isEmpty()) {
            System.out.println("No file was given.");
        } else {
            lz772slp = new LZ77_2_SLP();
            lz772slp.parseGzip(inputs);
        }
    }

    /**
     * SLP to LZ77 menu
     */
    public static void slp_2_lz77_menu() throws GeneralException {
        slp2lz77 = new SLP_2_LZ77();
        slp2lz77.main(null);
    }

}