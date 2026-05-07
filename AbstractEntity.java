import java.util.Scanner;

abstract class AbstractEntity {
    protected static final Scanner scanner = new Scanner(System.in);

    // Abstract methods each subclass must implement
    public abstract String getFileName();
    public abstract void loadFromFile();
    public abstract void saveToFile();
    public abstract void displayMenu();
    public abstract void add();
    public abstract void viewAll();
    public abstract void search();
    public abstract void update();
    public abstract void delete();

    // Utility method for safely reading String input
    protected static String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    // Utility method for safely reading integer input
    protected static int getIntInput(String prompt) {
        while (true) {
            try {
                if (!prompt.isEmpty()) System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Utility method for safely reading double input
    protected static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
