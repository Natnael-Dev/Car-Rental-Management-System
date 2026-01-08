import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Rental extends AbstractEntity {
    private Booking booking;
    private double  rentalCharges;
    private double  additionalFees;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;

    private static List<Rental> rentals = new ArrayList<>();
    private static final String RENTAL_FILE = "rentals.txt";

    public Rental(Booking booking, LocalDate rentalStartDate, LocalDate rentalEndDate,
                  double rentalCharges, double additionalFees) {
        this.booking        = booking;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate  = rentalEndDate;
        this.rentalCharges  = rentalCharges;
        this.additionalFees = additionalFees;
    }

    public Rental() {}

    // Getters
    public Booking   getBooking()        { return booking; }
    public double    getRentalCharges()  { return rentalCharges; }
    public double    getAdditionalFees() { return additionalFees; }
    public LocalDate getRentalStartDate(){ return rentalStartDate; }
    public LocalDate getRentalEndDate()  { return rentalEndDate; }

    // Setters
    public void setRentalCharges(double rentalCharges)   { this.rentalCharges = rentalCharges; }
    public void setAdditionalFees(double additionalFees) { this.additionalFees = additionalFees; }
    public void setRentalEndDate(LocalDate rentalEndDate){ this.rentalEndDate = rentalEndDate; }

    public static double calculateRentalCharges(Vehicle vehicle, LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0.0;
        long days = ChronoUnit.DAYS.between(start, end);
        return days * vehicle.getRentalRate();
    }

    @Override
    public String getFileName() { return RENTAL_FILE; }

    @Override
    public void loadFromFile() {
        rentals.clear();
        File file = new File(RENTAL_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created rentals file: " + RENTAL_FILE);
                return;
            } catch (IOException e) {
                System.out.println("Error creating rentals file: " + e.getMessage());
                return;
            }
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String    customerLicense  = parts[0];
                    String    vehicleVIN       = parts[1];
                    LocalDate rentalStartDate  = LocalDate.parse(parts[2], Booking.DATE_FORMATTER);
                    String    endDateStr       = parts[3];
                    LocalDate rentalEndDate    = endDateStr.equals("null") ? null
                                                 : LocalDate.parse(endDateStr, Booking.DATE_FORMATTER);
                    double rentalCharges       = Double.parseDouble(parts[4]);
                    double additionalFees      = Double.parseDouble(parts[5]);

                    Customer foundCustomer = Customer.getCustomers().stream()
                        .filter(c -> c.getLicenseNumber().equals(customerLicense))
                        .findFirst().orElse(null);
                    Vehicle foundVehicle = Vehicle.getVehicles().stream()
                        .filter(v -> v.getVin().equals(vehicleVIN))
                        .findFirst().orElse(null);

                    if (foundCustomer != null && foundVehicle != null) {
                        Booking foundBooking = Booking.getBookings().stream()
                            .filter(b -> b.getCustomer().equals(foundCustomer)
                                      && b.getVehicle().equals(foundVehicle))
                            .findFirst().orElse(null);
                        if (foundBooking != null) {
                            rentals.add(new Rental(foundBooking, rentalStartDate,
                                                   rentalEndDate, rentalCharges, additionalFees));
                            foundVehicle.setAvailability(rentalEndDate != null);
                        }
                    }
                } else {
                    System.out.println("Warning: Skipping invalid line in rentals file.");
                }
            }
        } catch (IOException | DateTimeParseException | NumberFormatException e) {
            System.out.println("Error loading rentals: " + e.getMessage());
        }
    }

    @Override
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(RENTAL_FILE)) {
            for (Rental rental : rentals) {
                String endDateStr = (rental.getRentalEndDate() == null)
                    ? "null"
                    : rental.getRentalEndDate().format(Booking.DATE_FORMATTER);
                writer.write(
                    rental.getBooking().getCustomer().getLicenseNumber()    + "," +
                    rental.getBooking().getVehicle().getVin()               + "," +
                    rental.getRentalStartDate().format(Booking.DATE_FORMATTER) + "," +
                    endDateStr                                               + "," +
                    rental.getRentalCharges()                               + "," +
                    rental.getAdditionalFees()                              + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving rentals to file: " + e.getMessage());
        }
    }

    @Override
    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- Rental Management Menu ---");
            System.out.println("1. Start New Rental");
            System.out.println("2. View All Rentals");
            System.out.println("3. Search Rental");
            System.out.println("4. Update Rental (End / Add Fees)");
            System.out.println("5. Delete Rental");
            System.out.println("6. Save Rentals to File");
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
                    System.out.println("Rentals saved to file.");
                }
                case 7 -> System.out.println("Returning to Main Menu.");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    @Override
    public void add() {
        System.out.println("\n--- Start New Rental ---");
        if (Booking.getBookings().isEmpty()) {
            System.out.println("No bookings available. Create a booking first.");
            return;
        }

        List<Booking> availableBookings = Booking.getBookings().stream()
            .filter(b -> b.getVehicle().isAvailable())
            .toList();

        if (availableBookings.isEmpty()) {
            System.out.println("No available bookings to start a rental.");
            return;
        }

        System.out.println("\nAvailable Bookings:");
        for (int i = 0; i < availableBookings.size(); i++) {
            System.out.println((i + 1) + ". " + availableBookings.get(i));
        }
        int bookingIndex = AbstractEntity.getIntInput("Select booking number to start rental");
        if (bookingIndex < 1 || bookingIndex > availableBookings.size()) {
            System.out.println("Invalid booking selection.");
            return;
        }
        Booking selectedBooking = availableBookings.get(bookingIndex - 1);

        LocalDate rentalStart = null;
        while (rentalStart == null) {
            String startDateStr = AbstractEntity.getStringInput("Enter actual rental start date (yyyy-MM-dd)");
            try {
                rentalStart = LocalDate.parse(startDateStr, Booking.DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        double estimatedCharges = calculateRentalCharges(
            selectedBooking.getVehicle(), rentalStart, selectedBooking.getEndDate());
        Rental newRental = new Rental(selectedBooking, rentalStart, null, estimatedCharges, 0.0);
        rentals.add(newRental);
        selectedBooking.getVehicle().setAvailability(false);
        System.out.println("Rental started successfully.");
        System.out.printf("Estimated charges: $%.2f%n", estimatedCharges);
    }

    @Override
    public void viewAll() {
        System.out.println("\n--- All Rentals ---");
        if (rentals.isEmpty()) {
            System.out.println("No rentals recorded.");
            return;
        }
        for (int i = 0; i < rentals.size(); i++) {
            System.out.println((i + 1) + ". " + rentals.get(i));
        }
    }

    @Override
    public void search() {
        System.out.println("\n--- Search Rental ---");
        System.out.println("1. Search by Customer License");
        System.out.println("2. Search by Vehicle VIN");
        int searchChoice = AbstractEntity.getIntInput("Enter your search criteria");

        switch (searchChoice) {
            case 1 -> {
                String searchLicense = AbstractEntity.getStringInput("Enter customer license");
                rentals.stream()
                    .filter(r -> r.getBooking().getCustomer().getLicenseNumber().equalsIgnoreCase(searchLicense))
                    .forEach(System.out::println);
            }
            case 2 -> {
                String searchVIN = AbstractEntity.getStringInput("Enter vehicle VIN");
                rentals.stream()
                    .filter(r -> r.getBooking().getVehicle().getVin().equalsIgnoreCase(searchVIN))
                    .forEach(System.out::println);
            }
            default -> System.out.println("Invalid search choice.");
        }
    }

    @Override
    public void update() {
        System.out.println("\n--- Update Rental ---");
        String licenseToUpdate = AbstractEntity.getStringInput("Enter customer license");
        String vinToUpdate     = AbstractEntity.getStringInput("Enter vehicle VIN");

        for (Rental rental : rentals) {
            if (rental.getBooking().getCustomer().getLicenseNumber().equalsIgnoreCase(licenseToUpdate) &&
                rental.getBooking().getVehicle().getVin().equalsIgnoreCase(vinToUpdate)) {

                System.out.println("Rental found.");
                System.out.println("1. End Rental");
                System.out.println("2. Add Additional Fees");
                int updateChoice = AbstractEntity.getIntInput("Enter your choice");

                switch (updateChoice) {
                    case 1 -> {
                        LocalDate rentalEnd = null;
                        while (rentalEnd == null) {
                            String endDateStr = AbstractEntity.getStringInput("Enter actual rental end date (yyyy-MM-dd)");
                            try {
                                rentalEnd = LocalDate.parse(endDateStr, Booking.DATE_FORMATTER);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                            }
                        }
                        rental.setRentalEndDate(rentalEnd);
                        double actualCharges = calculateRentalCharges(
                            rental.getBooking().getVehicle(), rental.getRentalStartDate(), rentalEnd);
                        rental.setRentalCharges(actualCharges);
                        rental.getBooking().getVehicle().setAvailability(true);
                        System.out.printf("Rental ended. Final charges: $%.2f%n", actualCharges);
                        saveToFile();
                    }
                    case 2 -> {
                        double fee = AbstractEntity.getDoubleInput("Enter additional fees to add");
                        rental.setAdditionalFees(rental.getAdditionalFees() + fee);
                        System.out.printf("Additional fees updated. Total additional fees: $%.2f%n",
                            rental.getAdditionalFees());
                        saveToFile();
                    }
                    default -> System.out.println("Invalid update choice.");
                }
                return;
            }
        }
        System.out.println("Rental not found for given customer and vehicle.");
    }

    @Override
    public void delete() {
        System.out.println("\n--- Delete Rental ---");
        String licenseToDelete = AbstractEntity.getStringInput("Enter customer license");
        String vinToDelete     = AbstractEntity.getStringInput("Enter vehicle VIN");

        boolean removed = rentals.removeIf(rental -> {
            if (rental.getBooking().getCustomer().getLicenseNumber().equalsIgnoreCase(licenseToDelete) &&
                rental.getBooking().getVehicle().getVin().equalsIgnoreCase(vinToDelete)) {
                rental.getBooking().getVehicle().setAvailability(true);
                return true;
            }
            return false;
        });

        if (removed) {
            System.out.println("Rental deleted successfully.");
            saveToFile();
        } else {
            System.out.println("No matching rental found.");
        }
    }

    @Override
    public String toString() {
        String endDateStr = (rentalEndDate == null) ? "Ongoing" : rentalEndDate.format(Booking.DATE_FORMATTER);
        return String.format("Customer: %s %s | Vehicle: %s (VIN: %s) | Start: %s | End: %s | Charges: $%.2f | Extra Fees: $%.2f",
            booking.getCustomer().getFirstname(), booking.getCustomer().getLastname(),
            booking.getVehicle().getMake() + " " + booking.getVehicle().getModel(),
            booking.getVehicle().getVin(),
            rentalStartDate.format(Booking.DATE_FORMATTER), endDateStr,
            rentalCharges, additionalFees);
    }

    public static List<Rental> getRentals() { return rentals; }
}
