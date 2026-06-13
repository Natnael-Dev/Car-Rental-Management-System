import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Vehicle extends AbstractEntity {
    private String make;
    private String model;
    private boolean available;
    private double rentalRate;
    private String vin;
    private String color;
    private int year;

    // Static list to hold all vehicle objects in memory
    private static List<Vehicle> vehicles = new ArrayList<>();
    private static final String VEHICLE_FILE = "vehicles.txt";

    public Vehicle(String make, String model, boolean available, double rentalRate, String vin, String color, int year) {
        this.make = make;
        this.model = model;
        this.available = available;
        this.rentalRate = rentalRate;
        this.vin = vin;
        this.color = color;
        this.year = year;
    }

    public Vehicle() {
        // Default constructor for loading from file
    }

    @Override
    public void add() {
        System.out.println("\n--- Add New Vehicle ---");
        String make = AbstractEntity.getStringInput("Enter make");
        String model = AbstractEntity.getStringInput("Enter model");
        double rentalRate = AbstractEntity.getDoubleInput("Enter rental rate (per day)");
        String vin = AbstractEntity.getStringInput("Enter VIN");
        String color = AbstractEntity.getStringInput("Enter color");
        int year = AbstractEntity.getIntInput("Enter year");

        Vehicle newVehicle = new Vehicle(make, model, true, rentalRate, vin, color, year);
        vehicles.add(newVehicle);
        System.out.println("Vehicle added successfully.");
        saveToFile();
    }

    @Override
    public void delete() {
        System.out.println("\n--- Delete Vehicle ---");
        String vinToDelete = AbstractEntity.getStringInput("Enter VIN of the vehicle to delete");
        boolean removed = vehicles.removeIf(vehicle -> vehicle.getVin().equalsIgnoreCase(vinToDelete));
        if (removed) {
            System.out.println("Vehicle with VIN '" + vinToDelete + "' deleted.");
            saveToFile();
        } else {
            System.out.println("Vehicle with VIN '" + vinToDelete + "' not found.");
        }
    }

    @Override
    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- Vehicle Management Menu ---");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View All Vehicles");
            System.out.println("3. Search Vehicle");
            System.out.println("4. Update Vehicle");
            System.out.println("5. Delete Vehicle");
            System.out.println("6. Save Vehicles to File");
            System.out.println("7. Rent a Vehicle");
            System.out.println("8. Calculate Rental Cost");
            System.out.println("9. Back to Main Menu");

            choice = AbstractEntity.getIntInput("Enter your choice");

            switch (choice) {
                case 1 -> add();
                case 2 -> viewAll();
                case 3 -> search();
                case 4 -> update();
                case 5 -> delete();
                case 6 -> {
                    saveToFile();
                    System.out.println("Vehicles saved to file.");
                }
                case 7 -> rentVehicle();
                case 8 -> calculateRentalCost();
                case 9 -> System.out.println("Returning to Main Menu.");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 9);
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getFileName() {
        return VEHICLE_FILE;
    }

    public String getMake()        { return make; }
    public String getModel()       { return model; }
    public double getRentalRate()  { return rentalRate; }
    public String getVin()         { return vin; }
    public String getColor()       { return color; }
    public int    getYear()        { return year; }

    public static List<Vehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void loadFromFile() {
        vehicles.clear();
        File file = new File(VEHICLE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created vehicles file: " + VEHICLE_FILE);
                return;
            } catch (IOException e) {
                System.out.println("Error creating vehicles file: " + e.getMessage());
                return;
            }
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setMake(parts[0]);
                    vehicle.setModel(parts[1]);
                    vehicle.setAvailability(Boolean.parseBoolean(parts[2]));
                    vehicle.setRentalRate(Double.parseDouble(parts[3]));
                    vehicle.setVin(parts[4]);
                    vehicle.setColor(parts[5]);
                    vehicle.setYear(Integer.parseInt(parts[6].trim()));
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles from file: " + e.getMessage());
        }
    }

    @Override
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(VEHICLE_FILE)) {
            for (Vehicle vehicle : vehicles) {
                writer.write(
                    vehicle.getMake()        + "," +
                    vehicle.getModel()       + "," +
                    vehicle.isAvailable()    + "," +
                    vehicle.getRentalRate()  + "," +
                    vehicle.getVin()         + "," +
                    vehicle.getColor()       + "," +
                    vehicle.getYear()        + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving vehicles to file: " + e.getMessage());
        }
    }

    @Override
    public void search() {
        System.out.println("\n--- Search Vehicle ---");
        System.out.println("1. Search by Make");
        System.out.println("2. Search by Model");
        System.out.println("3. Search by VIN");
        int searchChoice = AbstractEntity.getIntInput("Enter your search criteria");

        switch (searchChoice) {
            case 1 -> {
                String searchMake = AbstractEntity.getStringInput("Enter make to search");
                vehicles.stream()
                    .filter(v -> v.getMake().equalsIgnoreCase(searchMake))
                    .forEach(System.out::println);
            }
            case 2 -> {
                String searchModel = AbstractEntity.getStringInput("Enter model to search");
                vehicles.stream()
                    .filter(v -> v.getModel().equalsIgnoreCase(searchModel))
                    .forEach(System.out::println);
            }
            case 3 -> {
                String searchVIN = AbstractEntity.getStringInput("Enter VIN to search");
                vehicles.stream()
                    .filter(v -> v.getVin().equalsIgnoreCase(searchVIN))
                    .forEach(System.out::println);
            }
            default -> System.out.println("Invalid search criteria.");
        }
    }

    public void setAvailability(boolean available) { this.available = available; }
    public void setMake(String make)               { this.make = make; }
    public void setModel(String model)             { this.model = model; }
    public void setRentalRate(double rentalRate)   { this.rentalRate = rentalRate; }
    public void setVin(String vin)                 { this.vin = vin; }
    public void setColor(String color)             { this.color = color; }
    public void setYear(int year)                  { this.year = year; }

    @Override
    public String toString() {
        String status = available ? "Available" : "Rented";
        return String.format("VIN: %s | %d %s %s | Color: %s | Rate: $%.2f/day | Status: %s",
            vin, year, make, model, color, rentalRate, status);
    }

    @Override
    public void update() {
        System.out.println("\n--- Update Vehicle ---");
        String vinToUpdate = AbstractEntity.getStringInput("Enter VIN of the vehicle to update");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVin().equalsIgnoreCase(vinToUpdate)) {
                System.out.println("Vehicle found. Press Enter to keep the current value.");

                String newMake = AbstractEntity.getStringInput("New make (" + vehicle.getMake() + ")");
                String newModel = AbstractEntity.getStringInput("New model (" + vehicle.getModel() + ")");
                String availabilityStr = AbstractEntity.getStringInput("New availability true/false (" + vehicle.isAvailable() + ")");
                String newRentalRateStr = AbstractEntity.getStringInput("New rental rate (" + vehicle.getRentalRate() + ")");
                String newColor = AbstractEntity.getStringInput("New color (" + vehicle.getColor() + ")");
                String newYearStr = AbstractEntity.getStringInput("New year (" + vehicle.getYear() + ")");

                if (!newMake.isEmpty())          vehicle.setMake(newMake);
                if (!newModel.isEmpty())         vehicle.setModel(newModel);
                if (!availabilityStr.isEmpty())  vehicle.setAvailability(Boolean.parseBoolean(availabilityStr));
                if (!newRentalRateStr.isEmpty()) vehicle.setRentalRate(Double.parseDouble(newRentalRateStr));
                if (!newColor.isEmpty())         vehicle.setColor(newColor);
                if (!newYearStr.isEmpty())       vehicle.setYear(Integer.parseInt(newYearStr));

                System.out.println("Vehicle updated successfully.");
                saveToFile();
                return;
            }
        }
        System.out.println("Vehicle with VIN '" + vinToUpdate + "' not found.");
    }

    public void rentVehicle() {
        System.out.println("\n--- Rent a Vehicle ---");
        String vin = getStringInput("Enter VIN of vehicle to rent");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVin().equalsIgnoreCase(vin)) {
                if (!vehicle.isAvailable()) {
                    System.out.println("This vehicle is already rented.");
                    return;
                }
                vehicle.setAvailability(false);
                System.out.println("Vehicle rented successfully.");
                saveToFile();
                return;
            }
        }
        System.out.println("Vehicle not found.");
    }

    public void calculateRentalCost() {
        System.out.println("\n--- Calculate Rental Cost ---");
        String vin = getStringInput("Enter VIN");
        int days = getIntInput("Enter number of rental days");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVin().equalsIgnoreCase(vin)) {
                double total = vehicle.getRentalRate() * days;
                System.out.printf("Total rental cost for %d day(s): $%.2f%n", days, total);
                return;
            }
        }
        System.out.println("Vehicle not found.");
    }

    @Override
    public void viewAll() {
        System.out.println("\n--- All Vehicles ---");
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles in the system.");
            return;
        }
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.println((i + 1) + ". " + vehicles.get(i));
        }
        System.out.println("--------------------");
    }
}
