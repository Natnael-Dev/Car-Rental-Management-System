import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Booking extends AbstractEntity {
    private Customer customer;
    private Vehicle vehicle;
    private LocalDate startDate;
    private LocalDate endDate;

    private static List<Booking> bookings = new ArrayList<>();
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String BOOKING_FILE = "bookings.txt";

    public Booking(Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        this.customer  = customer;
        this.vehicle   = vehicle;
        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public Booking() {
        // Default constructor for loading from file
    }

    @Override
    public void add() {
        System.out.println("\n--- Add New Booking ---");

        if (Customer.getCustomers().isEmpty() || Vehicle.getVehicles().isEmpty()) {
            System.out.println("Please add customers and vehicles first.");
            return;
        }

        // Select customer
        List<Customer> allCustomers = Customer.getCustomers();
        System.out.println("\nAvailable Customers:");
        for (int i = 0; i < allCustomers.size(); i++) {
            System.out.println((i + 1) + ". " + allCustomers.get(i).getFirstname()
                + " " + allCustomers.get(i).getLastname()
                + " (License: " + allCustomers.get(i).getLicenseNumber() + ")");
        }
        int customerIndex = AbstractEntity.getIntInput("Select customer number");
        if (customerIndex < 1 || customerIndex > allCustomers.size()) {
            System.out.println("Invalid customer selection.");
            return;
        }
        Customer selectedCustomer = allCustomers.get(customerIndex - 1);

        // Select available vehicle
        List<Vehicle> availableVehicles = Vehicle.getVehicles().stream()
            .filter(Vehicle::isAvailable)
            .collect(Collectors.toList());

        if (availableVehicles.isEmpty()) {
            System.out.println("No vehicles currently available.");
            return;
        }

        System.out.println("\nAvailable Vehicles:");
        for (int i = 0; i < availableVehicles.size(); i++) {
            System.out.println((i + 1) + ". " + availableVehicles.get(i).getMake()
                + " " + availableVehicles.get(i).getModel()
                + " (VIN: " + availableVehicles.get(i).getVin() + ")");
        }
        int vehicleIndex = AbstractEntity.getIntInput("Select vehicle number");
        if (vehicleIndex < 1 || vehicleIndex > availableVehicles.size()) {
            System.out.println("Invalid vehicle selection.");
            return;
        }
        Vehicle selectedVehicle = availableVehicles.get(vehicleIndex - 1);

        // Parse start date
        LocalDate startDate = null;
        while (startDate == null) {
            String input = AbstractEntity.getStringInput("Enter start date (yyyy-MM-dd)");
            try {
                startDate = LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        // Parse end date
        LocalDate endDate = null;
        while (endDate == null) {
            String input = AbstractEntity.getStringInput("Enter end date (yyyy-MM-dd)");
            try {
                endDate = LocalDate.parse(input, DATE_FORMATTER);
                if (!endDate.isAfter(startDate)) {
                    System.out.println("End date must be after start date.");
                    endDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        // Check for overlapping bookings on the same vehicle
        for (Booking booking : bookings) {
            if (booking.getVehicle().getVin().equalsIgnoreCase(selectedVehicle.getVin())) {
                if (!(endDate.isBefore(booking.getStartDate()) || startDate.isAfter(booking.getEndDate()))) {
                    System.out.println("This vehicle is already booked during the selected dates.");
                    return;
                }
            }
        }

        Booking newBooking = new Booking(selectedCustomer, selectedVehicle, startDate, endDate);
        bookings.add(newBooking);
        selectedVehicle.setAvailability(false);
        System.out.println("Booking created successfully.");
        saveToFile();
    }

    @Override
    public void delete() {
        System.out.println("\n--- Delete Booking ---");
        String license = AbstractEntity.getStringInput("Enter customer license");
        String vin     = AbstractEntity.getStringInput("Enter vehicle VIN");

        boolean removed = bookings.removeIf(booking -> {
            if (booking.getCustomer().getLicenseNumber().equalsIgnoreCase(license)
                    && booking.getVehicle().getVin().equalsIgnoreCase(vin)) {
                booking.getVehicle().setAvailability(true);
                return true;
            }
            return false;
        });

        if (removed) {
            System.out.println("Booking deleted successfully.");
            saveToFile();
        } else {
            System.out.println("Booking not found.");
        }
    }

    @Override
    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- Booking Management Menu ---");
            System.out.println("1. Add Booking");
            System.out.println("2. View All Bookings");
            System.out.println("3. Search Booking");
            System.out.println("4. Update Booking");
            System.out.println("5. Delete Booking");
            System.out.println("6. Save Bookings to File");
            System.out.println("7. Back to Main Menu");

            choice = AbstractEntity.getIntInput("Enter your choice");

            switch (choice) {
                case 1 -> add();
                case 2 -> viewAll();
                case 3 -> search();
                case 4 -> update();
                case 5 -> delete();
                case 6 -> {
                    saveToFile();
                    System.out.println("Bookings saved to file.");
                }
                case 7 -> System.out.println("Returning to Main Menu.");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    @Override
    public void loadFromFile() {
        bookings.clear();
        File file = new File(BOOKING_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created bookings file: " + BOOKING_FILE);
            } catch (IOException e) {
                System.out.println("Error creating bookings file: " + e.getMessage());
            }
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String    customerLicense = parts[0];
                    String    vehicleVIN      = parts[1];
                    LocalDate start           = LocalDate.parse(parts[2], DATE_FORMATTER);
                    LocalDate end             = LocalDate.parse(parts[3], DATE_FORMATTER);

                    Customer customer = Customer.getCustomers().stream()
                        .filter(c -> c.getLicenseNumber().equals(customerLicense))
                        .findFirst().orElse(null);
                    Vehicle vehicle = Vehicle.getVehicles().stream()
                        .filter(v -> v.getVin().equals(vehicleVIN))
                        .findFirst().orElse(null);

                    if (customer != null && vehicle != null) {
                        bookings.add(new Booking(customer, vehicle, start, end));
                        vehicle.setAvailability(false);
                    }
                }
            }
        } catch (IOException | DateTimeParseException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }

    @Override
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(BOOKING_FILE)) {
            for (Booking b : bookings) {
                writer.write(
                    b.getCustomer().getLicenseNumber()       + "," +
                    b.getVehicle().getVin()                  + "," +
                    b.getStartDate().format(DATE_FORMATTER)  + "," +
                    b.getEndDate().format(DATE_FORMATTER)    + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    @Override
    public void search() {
        System.out.println("\n--- Search Booking ---");
        System.out.println("1. By Customer License");
        System.out.println("2. By Vehicle VIN");
        System.out.println("3. By Start Date");
        int searchChoice = AbstractEntity.getIntInput("Enter your search choice");

        switch (searchChoice) {
            case 1 -> {
                String license = AbstractEntity.getStringInput("Enter customer license");
                bookings.stream()
                    .filter(b -> b.getCustomer().getLicenseNumber().equalsIgnoreCase(license))
                    .forEach(System.out::println);
            }
            case 2 -> {
                String vin = AbstractEntity.getStringInput("Enter vehicle VIN");
                bookings.stream()
                    .filter(b -> b.getVehicle().getVin().equalsIgnoreCase(vin))
                    .forEach(System.out::println);
            }
            case 3 -> {
                String dateStr = AbstractEntity.getStringInput("Enter start date (yyyy-MM-dd)");
                try {
                    LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                    bookings.stream()
                        .filter(b -> b.getStartDate().equals(date))
                        .forEach(System.out::println);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    @Override
    public void update() {
        System.out.println("\n--- Update Booking ---");
        String license = AbstractEntity.getStringInput("Enter customer license");
        String vin     = AbstractEntity.getStringInput("Enter vehicle VIN");

        for (Booking b : bookings) {
            if (b.getCustomer().getLicenseNumber().equalsIgnoreCase(license)
                    && b.getVehicle().getVin().equalsIgnoreCase(vin)) {

                String newStartStr = AbstractEntity.getStringInput("New start date (yyyy-MM-dd) or Enter to skip");
                if (!newStartStr.isEmpty()) {
                    try {
                        b.setStartDate(LocalDate.parse(newStartStr, DATE_FORMATTER));
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid start date. Update cancelled.");
                        return;
                    }
                }

                String newEndStr = AbstractEntity.getStringInput("New end date (yyyy-MM-dd) or Enter to skip");
                if (!newEndStr.isEmpty()) {
                    try {
                        b.setEndDate(LocalDate.parse(newEndStr, DATE_FORMATTER));
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid end date. Update cancelled.");
                        return;
                    }
                }

                System.out.println("Booking updated successfully.");
                saveToFile();
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    @Override
    public void viewAll() {
        System.out.println("\n--- All Bookings ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            for (int i = 0; i < bookings.size(); i++) {
                System.out.println((i + 1) + ". " + bookings.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Customer: %s %s | Vehicle: %s %s (VIN: %s) | From: %s To: %s",
            customer.getFirstname(), customer.getLastname(),
            vehicle.getMake(), vehicle.getModel(), vehicle.getVin(),
            startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));
    }

    // Getters and Setters
    public Customer   getCustomer()  { return customer; }
    public Vehicle    getVehicle()   { return vehicle; }
    public LocalDate  getStartDate() { return startDate; }
    public LocalDate  getEndDate()   { return endDate; }

    public void setCustomer(Customer customer)   { this.customer = customer; }
    public void setVehicle(Vehicle vehicle)      { this.vehicle = vehicle; }
    public void setStartDate(LocalDate startDate){ this.startDate = startDate; }
    public void setEndDate(LocalDate endDate)    { this.endDate = endDate; }

    public static List<Booking> getBookings() { return bookings; }

    @Override
    public String getFileName() { return BOOKING_FILE; }
}
