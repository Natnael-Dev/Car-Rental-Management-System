# Car Rental Management System

A console-based **Car Rental Management System** built in Java using **Object-Oriented Programming (OOP)** principles.

## Features

* **Vehicle Management** — Add, view, search, update, delete vehicles; mark as rented or available
* **Customer Management** — Register and manage customer records with license tracking
* **Booking Management** — Create bookings with date validation and overlap detection
* **Rental Management** — Start/end rentals, calculate charges, track additional fees
* **File Persistence** — All data is saved to `.txt` files and reloaded on startup
* **Authentication** — Login system with attempt limiting

## OOP Concepts Used

|Concept|Where|
|-|-|
|Abstract Class|`AbstractEntity` — base class for all entities|
|Inheritance|`Vehicle`, `Customer`, `Booking`, `Rental` all extend `AbstractEntity`|
|Encapsulation|Private fields with getters/setters in every class|
|Polymorphism|`displayMenu()`, `add()`, `viewAll()` etc. overridden per class|
|Static members|Shared in-memory lists (`vehicles`, `customers`, etc.)|

## Project Structure

```
├── AbstractEntity.java     # Abstract base class
├── Vehicle.java            # Vehicle entity and management
├── Customer.java           # Customer entity and management
├── Booking.java            # Booking entity and management
├── Rental.java             # Rental entity and management
└── CarRentalSystem.java    # Main entry point
```

## How to Run

### Prerequisites

* Java JDK 17 or higher

### Compile

```bash
javac \\\*.java
```

### Run

```bash
java CarRentalSystem
```

### Login

```
Username: admin
Password: pass123
```

## Data Storage

The system automatically creates the following files on first run to persist data between sessions:

* `vehicles.txt`
* `customers.txt`
* `bookings.txt`
* `rentals.txt`

## Author

Developed as part of a 3rd year Computer Science OOP course project.





\## Contributing



Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.



\## License



This project is for educational purposes as part of a 3rd year Computer Science OOP course.



\## Sample Data



On first run the system auto-creates these data files:

\- `vehicles.txt` — stores all vehicle records

\- `customers.txt` — stores all customer records  

\- `bookings.txt` — stores all booking records

\- `rentals.txt` — stores all rental records

