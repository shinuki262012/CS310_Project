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
        menu();
    }

    /**
     * Main menu
     */
    public static void menu() {
        char op; // option
        while (true) {
            System.out.println("\n Menu: \n");
            System.out.println("==================");
            System.out.println("1 - Texts to SLP");
            System.out.println("2 - LZ77 to SLP");
            System.out.println("3 - SLP to LZ77");
            System.out.println("q - Exit");
            System.out.println("==================");
            System.out.println("Enter an option to start: \n");

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
        // TODO: file name check ?
        String file = input.nextLine();
        String input = "";
        SLP = new Text_to_SLP();
        try {
            input = new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            System.out.println("Failed to read file.");
        }
        try {
            PrintStream stdout = System.out;
            PrintStream stream = new PrintStream(file + ".txt");
            System.setOut(stream);
            SLP.TtoG(input);
            System.setOut(stdout);
            System.out.println("Output successfully saved to " + file + ".txt");

        } catch (Exception e) {

        }

        // try {
        // FileOutputStream fileOut = new FileOutputStream(file + ".txt");
        // ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        // objectOut.writeBytes(output);

        // System.out.println("2");
        // objectOut.flush();
        // objectOut.close();
        // System.out.println("Output successfully saved to " + file + ".txt");
        // } catch (IOException e) {
        // System.err.println(e);
        // System.out.println("Unable to output to file.");
        // }

    }

}