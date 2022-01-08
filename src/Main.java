import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    static Scanner input;
    static Text_to_SLP SLP;

    /**
     * Display the main menu
     * 
     * @param args
     */
    public static void main(String[] args) {
        input = new Scanner(System.in);
        // menu();
        TtoSLPmenu();
    }

    /**
     * Main menu
     */
    public static void menu() {
        char op; // option
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
            op = inputs.charAt(0);
            switch (op) {
                case '1':
                    System.out.println("Option 1 selected\n");
                    TtoSLPmenu();
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

    public static void TtoSLPmenu() {
        System.out.println("Choose the file to convert: ");
        // TODO: file name check
        String file = input.nextLine();
        // String file = "..\\test\\zip";
        // String file = "..\\test\\alice29";
        // String file = "..\\test\\enwik7";
        // String file = "..\\test\\128random";
        String input = "";
        SLP = new Text_to_SLP();
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
            System.out.println(input.length());
            SLP.TtoG(input);
            System.setOut(stdout);
            System.out.println("Output successfully saved to " + file + ".slp");
        } catch (Exception e) {

        }
    }

}