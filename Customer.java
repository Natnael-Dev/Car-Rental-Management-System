import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Customer extends AbstractEntity {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String address;
    private String sex;
    private String contactInfo; // Emergency contact or alternate contact person
    private String licenseNumber;

    private static List<Customer> customers = new ArrayList<>();
    private static final String CUSTOMER_FILE = "customers.txt";

    public Customer(String firstname, String lastname, String email, String phoneNumber,
                    String address, String sex, String contactInfo, String licenseNumber) {
        this.firstname     = firstname;
        this.lastname      = lastname;
        this.email         = email;
        this.phoneNumber   = phoneNumber;
        this.address       = address;
        this.sex           = sex;
        this.contactInfo   = contactInfo;
        this.licenseNumber = licenseNumber;
    }

    public Customer() {
        // Default constructor for loading from file
    }

    // Getters
    public String getFirstname()     { return firstname; }
    public String getLastname()      { return lastname; }
    public String getEmail()         { return email; }
    public String getPhoneNumber()   { return phoneNumber; }
    public String getAddress()       { return address; }
    public String getSex()           { return sex; }
    public String getContactInfo()   { return contactInfo; }
    public String getLicenseNumber() { return licenseNumber; }

    // Setters
    public void setFirstname(String firstname)         { this.firstname = firstname; }
    public void setLastname(String lastname)           { this.lastname = lastname; }
    public void setEmail(String email)                 { this.email = email; }
    public void setPhoneNumber(String phoneNumber)     { this.phoneNumber = phoneNumber; }
    public void setAddress(String address)             { this.address = address; }
    public void setSex(String sex)                     { this.sex = sex; }
    public void setContactInfo(String contactInfo)     { this.contactInfo = contactInfo; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Override
    public void add() {
        System.out.println("\n--- Add Customer ---");
        String firstname     = AbstractEntity.getStringInput("Enter first name");
        String lastname      = AbstractEntity.getStringInput("Enter last name");
        String email         = AbstractEntity.getStringInput("Enter email");
        String phoneNumber   = AbstractEntity.getStringInput("Enter phone number");
        String address       = AbstractEntity.getStringInput("Enter address");
        String sex           = AbstractEntity.getStringInput("Enter sex");
        String contactInfo   = AbstractEntity.getStringInput("Enter emergency contact info");
        String licenseNumber = AbstractEntity.getStringInput("Enter license number");

        Customer newCustomer = new Customer(firstname, lastname, email, phoneNumber,
                                            address, sex, contactInfo, licenseNumber);
        customers.add(newCustomer);
        System.out.println("Customer added successfully.");
        saveToFile();
    }

    @Override
    public void delete() {
        System.out.println("\n--- Delete Customer ---");
        String licenseToDelete = AbstractEntity.getStringInput("Enter license number of the customer to delete");

        boolean removed = customers.removeIf(c ->
            c.getLicenseNumber() != null && c.getLicenseNumber().equalsIgnoreCase(licenseToDelete)
        );

        if (removed) {
            System.out.println("Customer deleted successfully.");
            saveToFile();
        } else {
            System.out.println("Customer with license '" + licenseToDelete + "' not found.");
        }
    }

    @Override
    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- Customer Management Menu ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Search Customer");
            System.out.println("4. Update Customer");
            System.out.println("5. Delete Customer");
            System.out.println("6. Save Customers to File");
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
                    System.out.println("Customers saved to file.");
                }
                case 7 -> System.out.println("Returning to Main Menu.");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    @Override
    public String getFileName() {
        return CUSTOMER_FILE;
    }

    public static List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public void loadFromFile() {
        customers.clear();
        File file = new File(CUSTOMER_FILE);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Customer file created: " + CUSTOMER_FILE);
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
                return;
            }
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length == 8) {
                    customers.add(new Customer(
                        parts[0], parts[1], parts[2], parts[3],
                        parts[4], parts[5], parts[6], parts[7]
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    @Override
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(CUSTOMER_FILE)) {
            for (Customer c : customers) {
                writer.write(
                    c.getFirstname()     + "," +
                    c.getLastname()      + "," +
                    c.getEmail()         + "," +
                    c.getPhoneNumber()   + "," +
                    c.getAddress()       + "," +
                    c.getSex()           + "," +
                    c.getContactInfo()   + "," +
                    c.getLicenseNumber() + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }

    @Override
    public void search() {
        System.out.println("\n--- Search Customer ---");
        System.out.println("1. Search by First Name");
        System.out.println("2. Search by Last Name");
        System.out.println("3. Search by License Number");
        int option = AbstractEntity.getIntInput("Choose search option");

        switch (option) {
            case 1 -> {
                String firstName = AbstractEntity.getStringInput("Enter first name");
                customers.stream()
                    .filter(c -> c.getFirstname() != null && c.getFirstname().equalsIgnoreCase(firstName))
                    .forEach(System.out::println);
            }
            case 2 -> {
                String lastName = AbstractEntity.getStringInput("Enter last name");
                customers.stream()
                    .filter(c -> c.getLastname() != null && c.getLastname().equalsIgnoreCase(lastName))
                    .forEach(System.out::println);
            }
            case 3 -> {
                String license = AbstractEntity.getStringInput("Enter license number");
                customers.stream()
                    .filter(c -> c.getLicenseNumber() != null && c.getLicenseNumber().equalsIgnoreCase(license))
                    .forEach(System.out::println);
            }
            default -> System.out.println("Invalid search option.");
        }
    }

    @Override
    public void update() {
        System.out.println("\n--- Update Customer ---");
        String licenseToUpdate = AbstractEntity.getStringInput("Enter license number of customer to update");

        for (Customer c : customers) {
            if (c.getLicenseNumber() != null && c.getLicenseNumber().equalsIgnoreCase(licenseToUpdate)) {
                System.out.println("Customer found. Press Enter to keep the current value.");

                String newFirst   = AbstractEntity.getStringInput("New first name (" + c.getFirstname() + ")");
                String newLast    = AbstractEntity.getStringInput("New last name (" + c.getLastname() + ")");
                String newEmail   = AbstractEntity.getStringInput("New email (" + c.getEmail() + ")");
                String newPhone   = AbstractEntity.getStringInput("New phone (" + c.getPhoneNumber() + ")");
                String newAddress = AbstractEntity.getStringInput("New address (" + c.getAddress() + ")");
                String newSex     = AbstractEntity.getStringInput("New sex (" + c.getSex() + ")");
                String newContact = AbstractEntity.getStringInput("New emergency contact (" + c.getContactInfo() + ")");

                if (!newFirst.isEmpty())   c.setFirstname(newFirst);
                if (!newLast.isEmpty())    c.setLastname(newLast);
                if (!newEmail.isEmpty())   c.setEmail(newEmail);
                if (!newPhone.isEmpty())   c.setPhoneNumber(newPhone);
                if (!newAddress.isEmpty()) c.setAddress(newAddress);
                if (!newSex.isEmpty())     c.setSex(newSex);
                if (!newContact.isEmpty()) c.setContactInfo(newContact);

                System.out.println("Customer updated successfully.");
                saveToFile();
                return;
            }
        }
        System.out.println("Customer with license '" + licenseToUpdate + "' not found.");
    }

    @Override
    public void viewAll() {
        System.out.println("\n--- All Customers ---");
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            for (int i = 0; i < customers.size(); i++) {
                System.out.println((i + 1) + ". " + customers.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Name: %s %s | License: %s | Email: %s | Phone: %s",
            firstname, lastname, licenseNumber, email, phoneNumber);
    }
}
