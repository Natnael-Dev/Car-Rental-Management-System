import java.util.Scanner;

class CarRentalSystem {
    private static final Scanner scanner = new Scanner(System.in);

    private static final Vehicle  vehicleManager  = new Vehicle();
    private static final Customer customerManager = new Customer();
    private static final Booking  bookingManager  = new Booking();
    private static final Rental   rentalManager   = new Rental();

  
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "pass123";

    public static void main(String[] args) {
        loadAllData();

        if (!authenticateUser()) {
            System.out.println("Authentication failed. Exiting system.");
            return;
        }

        runMainMenu();
        scanner.close();
    }

    private static void loadAllData() {
        try {
            System.out.println("\nLoading system data...");
            vehicleManager.loadFromFile();
            customerManager.loadFromFile();
            bookingManager.loadFromFile();
            rentalManager.loadFromFile();
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void runMainMenu() {
        int choice;
        do {
            displayMainMenu();
            choice = AbstractEntity.getIntInput("Enter your choice");

            switch (choice) {
                case 1 -> vehicleManager.displayMenu();
                case 2 -> customerManager.displayMenu();
                case 3 -> bookingManager.displayMenu();
                case 4 -> rentalManager.displayMenu();
                case 5 -> saveAllData();
                case 0 -> System.out.println("Exiting Car Rental System. Goodbye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private static void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println("       Car Rental Management System     ");
        System.out.println("========================================");
        System.out.println("  1. Vehicle Management");
        System.out.println("  2. Customer Management");
        System.out.println("  3. Booking Management");
        System.out.println("  4. Rental Management");
        System.out.println("  5. Save All Data");
        System.out.println("  0. Exit");
        System.out.println("========================================");
    }

    private static void saveAllData() {
        System.out.println("\n--- Saving All Data ---");
        try {
            vehicleManager.saveToFile();
            customerManager.saveToFile();
            bookingManager.saveToFile();
            rentalManager.saveToFile();
            System.out.println("All data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static boolean authenticateUser() {
        final int MAX_ATTEMPTS = 3;
        int attempts = 0;

        System.out.println("\n============= Login =============");
        System.out.println("  Demo credentials:");
        System.out.println("  Username : admin");
        System.out.println("  Password : pass123");
        System.out.println("=================================");

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("\nEnter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                System.out.println("Login successful. Welcome, " + ADMIN_USERNAME + "!");
                return true;
            }

            attempts++;
            int remaining = MAX_ATTEMPTS - attempts;
            if (remaining > 0) {
                System.out.println("Incorrect username or password. " + remaining + " attempt(s) remaining.");
            } else {
                System.out.println("Too many failed attempts. Exiting system.");
            }
        }

        return false;
    }
}
