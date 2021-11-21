import java.util.*;
import java.io.*;

public class Main {

    /**
     * Display the main menu
     * 
     * @param args
     */
    public static void main(String[] args) {
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
}