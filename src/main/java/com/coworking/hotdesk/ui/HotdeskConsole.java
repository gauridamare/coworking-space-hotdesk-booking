
package com.coworking.hotdesk.ui;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.dao.EmptyResultDataAccessException;

import com.coworking.hotdesk.entity.Booking;
import com.coworking.hotdesk.entity.User;
import com.coworking.hotdesk.entity.Workspace;
import com.coworking.hotdesk.service.BookingService;
import com.coworking.hotdesk.service.UserService;
import com.coworking.hotdesk.service.WorkspaceService;

@Component
@ConditionalOnProperty(
    name = "hotdesk.console.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class HotdeskConsole implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);

    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final BookingService bookingService;

    public HotdeskConsole(
            WorkspaceService workspaceService,
            UserService userService,
            BookingService bookingService) {

        this.workspaceService = workspaceService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @Override
    public void run(String... args) {

        int choice;

        do {
            showMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewAllWorkspaces();
                    break;
                case 2:
                    registerNewUser();
                    break;
                case 3:
                    viewUserDetails();
                    break;
                case 4:
                    bookWorkspace();
                    break;
                case 5:
                    cancelBooking();
                    break;
                case 6:
                    viewAllBookings();
                    break;
                case 7:
                    viewRevenueReport();
                    break;
                case 8:
                    registerNewWorkspace();
                    break;
                case 9:
                    checkAvailableWorkspaces();
                    break;
                case 0:
                    System.out.println(
                            "Thank you for using Hot Desk Booking Engine!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 0);
    }

    private void viewAllWorkspaces() {

        List<Workspace> workspaces =
                workspaceService.getAllActiveWorkspaces();

        System.out.println("\n========== AVAILABLE WORKSPACES ==========");

        if (workspaces.isEmpty()) {
            System.out.println("No active workspaces found.");
            return;
        }

        for (Workspace workspace : workspaces) {
            System.out.println("----------------------------------");
            System.out.println("Space ID    : " + workspace.getSpaceId());
            System.out.println("Space Name  : " + workspace.getSpaceName());
            System.out.println("Space Type  : " + workspace.getSpaceType());
            System.out.println("Hourly Rate : " + workspace.getHourlyRate());
            System.out.println("Active      : " + workspace.isActive());
        }

        System.out.println("----------------------------------");
    }

    private void registerNewUser() {

        scanner.nextLine();

        System.out.println("\n===== REGISTER NEW USER =====");

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter initial wallet balance: ");
        BigDecimal walletBalance = scanner.nextBigDecimal();

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setWalletBalance(walletBalance);

        userService.addUser(user);

        System.out.println("\nUser registered successfully!");
    }

    private void viewUserDetails() {

        System.out.println("\n===== VIEW USER DETAILS =====");

        System.out.print("Enter User ID: ");
        Long userId = scanner.nextLong();

        try {
            User user = userService.getUserById(userId);

            System.out.println("\n========== USER DETAILS ==========");
            System.out.println("User ID        : " + user.getUserId());
            System.out.println("Full Name      : " + user.getFullName());
            System.out.println("Email          : " + user.getEmail());
            System.out.println("Wallet Balance : " + user.getWalletBalance());
            System.out.println("==================================");

        } catch (EmptyResultDataAccessException e) {
            System.out.println("User not found with ID: " + userId);
        }
    }

    private void bookWorkspace() {

        scanner.nextLine();

        System.out.println("\n===== BOOK A WORKSPACE =====");

        try {
            System.out.print("Enter User ID: ");
            Long userId = scanner.nextLong();

            System.out.print("Enter Workspace ID: ");
            Long spaceId = scanner.nextLong();

            scanner.nextLine();

            System.out.print("Enter booking date (YYYY-MM-DD): ");
            LocalDate bookingDate =
                    LocalDate.parse(scanner.nextLine().trim());

            System.out.println("Enter slot hour (0-23):");
            System.out.println("Example: 9 = 9 AM, 14 = 2 PM");

            System.out.print("Enter hour: ");
            int slotHour = scanner.nextInt();

            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setSpaceId(spaceId);
            booking.setBookingDate(bookingDate);
            booking.setSlotHour(slotHour);

            bookingService.createBooking(booking);

            System.out.println("\n==================================");
            System.out.println("BOOKING CONFIRMED SUCCESSFULLY!");
            System.out.println("User ID       : " + userId);
            System.out.println("Workspace ID  : " + spaceId);
            System.out.println("Booking Date  : " + bookingDate);
            System.out.println("Slot Hour     : " + slotHour);
            System.out.println("Total Cost    : " + booking.getTotalCost());
            System.out.println("Status        : " + booking.getBookingStatus());
            System.out.println("==================================");

        } catch (DateTimeParseException e) {
            System.out.println(
                    "Invalid date format. Please use YYYY-MM-DD.");

        } catch (EmptyResultDataAccessException e) {
            System.out.println(
                    "User ID or Workspace ID does not exist.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    private void cancelBooking() {

        scanner.nextLine();

        System.out.println("\n===== CANCEL BOOKING =====");

        System.out.print("Enter Booking ID: ");
        Long bookingId = scanner.nextLong();

        try {
            bookingService.cancelBooking(bookingId);

            System.out.println("\n==================================");
            System.out.println("BOOKING CANCELLED SUCCESSFULLY!");
            System.out.println("Booking ID : " + bookingId);
            System.out.println("Status     : CANCELLED");
            System.out.println("Wallet refund credited successfully!");
            System.out.println("==================================");

        } catch (EmptyResultDataAccessException e) {
            System.out.println("Booking not found with ID: " + bookingId);

        } catch (IllegalArgumentException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }

    // View all bookings from the database
    private void viewAllBookings() {

        List<Booking> bookings = bookingService.getAllBookings();

        System.out.println("\n========== ALL BOOKINGS ==========");

        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookings) {

            System.out.println("----------------------------------");
            System.out.println("Booking ID   : " + booking.getBookingId());
            System.out.println("User ID      : " + booking.getUserId());
            System.out.println("Workspace ID : " + booking.getSpaceId());
            System.out.println("Date         : " + booking.getBookingDate());
            System.out.println("Slot Hour    : " + booking.getSlotHour());
            System.out.println("Total Cost   : " + booking.getTotalCost());
            System.out.println("Status       : " + booking.getBookingStatus());
        }

        System.out.println("----------------------------------");
    }

    private void registerNewWorkspace() {

        scanner.nextLine();

        System.out.println("\n===== REGISTER NEW WORKSPACE =====");

        System.out.print("Enter workspace name: ");
        String spaceName = scanner.nextLine();

        System.out.println("Available workspace types:");
        System.out.println("1. HOT_DESK");
        System.out.println("2. DEDICATED_DESK");
        System.out.println("3. MEETING_ROOM");
        System.out.println("4. CABIN");

        System.out.print("Enter workspace type: ");
        String spaceType = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter hourly rate: ");
        BigDecimal hourlyRate = scanner.nextBigDecimal();

        if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Hourly rate must be greater than zero.");
            return;
        }

        Workspace workspace = new Workspace();
        workspace.setSpaceName(spaceName);
        workspace.setSpaceType(spaceType);
        workspace.setHourlyRate(hourlyRate);
        workspace.setActive(true);

        workspaceService.addWorkspace(workspace);

        System.out.println("\nWorkspace registered successfully!");
    }

    private void checkAvailableWorkspaces() {

        scanner.nextLine();

        System.out.println("\n===== CHECK SLOT AVAILABILITY =====");

        try {
            System.out.print("Enter booking date (YYYY-MM-DD): ");
            LocalDate bookingDate =
                    LocalDate.parse(scanner.nextLine().trim());

            System.out.println("Enter slot hour (0-23):");
            System.out.println("Example: 9 = 9 AM, 14 = 2 PM");

            System.out.print("Enter hour: ");
            int slotHour = scanner.nextInt();

            List<Workspace> workspaces =
                    workspaceService.getAvailableWorkspaces(
                            bookingDate, slotHour);

            System.out.println("\n===== AVAILABLE WORKSPACES =====");

            if (workspaces.isEmpty()) {
                System.out.println(
                        "No workspaces available for this date and hour.");
                return;
            }

            for (Workspace workspace : workspaces) {
                System.out.println("----------------------------------");
                System.out.println("Space ID    : " + workspace.getSpaceId());
                System.out.println("Space Name  : " + workspace.getSpaceName());
                System.out.println("Space Type  : " + workspace.getSpaceType());
                System.out.println("Hourly Rate : " + workspace.getHourlyRate());
            }

            System.out.println("----------------------------------");

        } catch (DateTimeParseException e) {
            System.out.println(
                    "Invalid date format. Please use YYYY-MM-DD.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showMenu() {

        System.out.println("\n===== HOT DESK BOOKING ENGINE =====");
        System.out.println("1. View All Workspaces");
        System.out.println("2. Register New User");
        System.out.println("3. View User Details");
        System.out.println("4. Book a Workspace");
        System.out.println("5. Cancel Booking");
        System.out.println("6. View All Bookings");
        System.out.println("7. View Revenue Report");
        System.out.println("8. Register New Workspace");
        System.out.println("9. Check Available Workspaces");
        System.out.println("0. Exit");
    }
     // Display revenue report by workspace type
private void viewRevenueReport() {

    List<Map<String, Object>> revenueList =
            bookingService.getRevenueBySpaceType();

    System.out.println("\n========== REVENUE REPORT ==========");

    if (revenueList.isEmpty()) {
        System.out.println("No confirmed bookings. Revenue is currently zero.");
        return;
    }

    BigDecimal grandTotal = BigDecimal.ZERO;

    for (Map<String, Object> row : revenueList) {

        String spaceType = row.get("space_type").toString();

        BigDecimal revenue =
                (BigDecimal) row.get("total_revenue");

        System.out.println("----------------------------------");
        System.out.println("Workspace Type : " + spaceType);
        System.out.println("Total Revenue  : Rs. " + revenue);

        grandTotal = grandTotal.add(revenue);
    }

    System.out.println("----------------------------------");
    System.out.println("GRAND TOTAL    : Rs. " + grandTotal);
    System.out.println("==================================");
}
}
