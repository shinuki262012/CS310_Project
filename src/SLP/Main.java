package slp;

import java.util.*;

import javax.swing.plaf.synth.SynthSpinnerUI;

import slp.text_slp.Folca;
import slp.text_slp.Text_2_SLP;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static Scanner inputScanner;
    static Text_2_SLP text2slp;
    static Folca folca;

    /**
     * Display the main menu
     * 
     * @param args
     */
    public static void main(String[] args) {
        inputScanner = new Scanner(System.in);
        menu();
        inputScanner.close();
    }

    /**
     * Main menu
     */
    public static void menu() {
        String inputs;
        while (true) {
            System.out.println("\nMenu: ");
            System.out.println("==================");
            System.out.println("1 - Texts to SLP");
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
                    break;
                case '3':
                    System.out.println("Option 3 selected.\n");
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

    public static void lz77_2_slp_menu() {

    }

}