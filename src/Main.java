import java.util.*;

import javax.swing.plaf.synth.SynthSpinnerUI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    static Scanner input;
    static Text_to_SLP slp;
    static Folca folca;

    /**
     * Display the main menu
     * 
     * @param args
     */
    public static void main(String[] args) {
        input = new Scanner(System.in);
        // menu();
        txt_2_SLP_menu();
    }

    /**
     * Main menu
     */
    public static void menu() {
        while (true) {
            System.out.println("\n Menu: ");
            System.out.println("==================");
            System.out.println("1 - Texts to SLP");
            System.out.println("2 - LZ77 to SLP");
            System.out.println("3 - SLP to LZ77");
            System.out.println("q - Exit");
            System.out.println("==================");
            System.out.println("Enter an option to start: ");

            Scanner input = new Scanner(System.in);
            String inputs = input.nextLine().toString().trim();
            if (inputs.isEmpty()) {
                System.out.println("No option was given\n");
                break;
            }
            input.close();
            switch (inputs.charAt(0)) {
                case '1':
                    System.out.println("Option 1 selected\n");
                    txt_2_SLP_menu();
                    break;
                case '2':
                    System.out.println("Option 2 selected\n");
                    break;
                case '3':
                    System.out.println("Option 3 selected\n");
                    break;
                case 'q':
                    System.out.println("Exiting...\n");
                    break;
                default:
                    System.out.println("Please enter a valid option.\n");
                    break;
            }
            break;
        }
    }

    /**
     * Text to SLP menu
     */
    public static void txt_2_SLP_menu() {
        System.out.println("Choose the online or offline version of the compression algorithm: ");
        System.out.println("1 - offline \n2 - online");
        Scanner choice = new Scanner(System.in);
        char option = choice.nextLine().toString().trim().charAt(0);
        // choice.close();
        if (option == '1') {
            // Offline version
            System.out.println("Choose the file to convert: ");
            // TODO: file name check
            String file = input.nextLine();
            // String file = "..\\test\\zip";
            // String file = "..\\test\\alice29";
            // String file = "..\\test\\enwik7";
            // String file = "..\\test\\128random";
            slp = new Text_to_SLP();
            while (true) {
                // Display options
                System.out.println("1 - text to succinct grammar");
                System.out.println("2 - text to cfg");
                System.out.println("3 - cfg to succinct grammar");
                System.out.println("4 - cfg to grammar tree");
                System.out.println("5 - succinct grammar to text");
                System.out.println("6 - cfg to text");
                System.out.println("q - Exit");
                Scanner input = new Scanner(System.in);
                String inputs = input.nextLine().toString().trim();
                if (inputs.isEmpty()) {
                    System.out.println("No option was given\n");
                    break;
                }
                switch (inputs.charAt(0)) {
                    case '1':
                        break;
                    case '2':
                        break;
                    case '3':
                        break;
                    case 'q':
                        break;
                    default:
                        System.out.println("Please enter a valid option.\n");
                        break;
                }
                break;
            }
            // TODO: move this part to the Text_to_slp.java
            String input = "";
            try {
                input = new String(Files.readAllBytes(Paths.get(file)));
            } catch (IOException e) {
                System.out.println("Failed to read file.");
            }
            try {
                PrintStream stdout = System.out;
                // Output to a file
                PrintStream stream = new PrintStream(file + ".slp");
                System.setOut(stream);
                System.out.println(input.length()); // DELETE
                slp.TtoG(input);
                System.setOut(stdout);
                System.out.println("Output successfully saved to " + file + ".slp");
            } catch (Exception e) {
                // TODO: catch
            }

        } else if (option == '2') {
            // Online version
            folca = new Folca();
            folca.main(null);
        } else {
            System.out.println("Please enter a valid option");
        }
        choice.close();
    }

}