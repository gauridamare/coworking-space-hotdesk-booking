# Co-Working Space Hot-Desk Booking Engine

A Java-based console application developed using Spring Boot, Spring JDBC, and MySQL to manage co-working space reservations, users, wallet balances, and booking operations.

## Features

- View all available workspaces
- Register new users
- View user details and wallet balance
- Book a workspace for a specific date and time
- Cancel confirmed bookings
- Automatically refund wallet balance after cancellation
- View all booking records
- Generate workspace revenue reports
- Register new workspaces
- Check workspace availability
- Prevent duplicate bookings for the same workspace, date, and time slot

## Technologies Used

- Java 17
- Spring Boot
- Spring JDBC
- MySQL 8
- Maven
- JDBC Template
- Git and GitHub
- JUnit and Mockito

## Project Architecture

```text
src/main/java/com/coworking/hotdesk/
│
├── config/
├── dao/
├── entity/
├── rowmapper/
├── service/
├── ui/
└── HotdeskApplication.java

src/main/resources/
├── application.properties
├── schema.sql
└── data.sql
```

## Database Setup

1. Install MySQL 8.
2. Open MySQL Workbench.
3. Execute `schema.sql` to create the database and tables.
4. Execute `data.sql` once to insert sample workspaces and users.
5. Configure your MySQL username and password in `application.properties`.

**Important:** Do not upload your actual database password or `application.properties` containing credentials to GitHub.

## Run the Application

Make sure MySQL is running, then open the project directory in the terminal.

Run:

```powershell
.\mvnw.cmd clean install
```

Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

## Console Menu

```text
===== HOT DESK BOOKING ENGINE =====

1. View All Workspaces
2. Register New User
3. View User Details
4. Book a Workspace
5. Cancel Booking
6. View All Bookings
7. View Revenue Report
8. Register New Workspace
9. Check Available Workspaces
0. Exit
```

## Testing

Run automated tests using:

```powershell
.\mvnw.cmd clean test
```

## Developer

**Gauri Damare**

GitHub: https://github.com/gauridamare

## Project Status

Core booking, cancellation, wallet refund, slot availability, and duplicate-booking functionality implemented and manually tested.
