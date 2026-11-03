
package com.coworking.hotdesk.service;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.coworking.hotdesk.dao.BookingDao;
import com.coworking.hotdesk.dao.UserDao;
import com.coworking.hotdesk.dao.WorkspaceDao;

import com.coworking.hotdesk.entity.Booking;
import com.coworking.hotdesk.entity.User;
import com.coworking.hotdesk.entity.Workspace;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final WorkspaceDao workspaceDao;

    public BookingService(
            BookingDao bookingDao,
            UserDao userDao,
            WorkspaceDao workspaceDao) {

        this.bookingDao = bookingDao;
        this.userDao = userDao;
        this.workspaceDao = workspaceDao;
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingDao.getAllBookings();
    }

    // Get revenue grouped by workspace type
    public List<Map<String, Object>> getRevenueBySpaceType() {
        return bookingDao.getRevenueBySpaceType();
    }

    // Create a booking
    @Transactional
    public void createBooking(Booking booking) {

        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking details are required!");
        }

        if (booking.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required!");
        }

        if (booking.getSpaceId() == null) {
            throw new IllegalArgumentException("Workspace ID is required!");
        }

        if (booking.getBookingDate() == null) {
            throw new IllegalArgumentException("Booking date is required!");
        }

        if (booking.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Booking date cannot be in the past!");
        }

        if (booking.getSlotHour() == null ||
                booking.getSlotHour() < 0 ||
                booking.getSlotHour() > 23) {

            throw new IllegalArgumentException(
                    "Slot hour must be between 0 and 23!");
        }

        // Check whether user exists
        User user = userDao.getUserById(booking.getUserId());

        if (user == null) {
            throw new IllegalArgumentException("User not found!");
        }

        // Check whether workspace exists
        Workspace workspace =
                workspaceDao.getWorkspaceById(booking.getSpaceId());

        if (workspace == null) {
            throw new IllegalArgumentException("Workspace not found!");
        }

        if (!workspace.isActive()) {
            throw new IllegalArgumentException(
                    "Workspace is not active!");
        }

        // Check workspace availability
        boolean available = bookingDao.isSlotAvailable(
                booking.getSpaceId(),
                booking.getBookingDate(),
                booking.getSlotHour()
        );

        if (!available) {
            throw new IllegalArgumentException(
                    "This workspace slot is already booked!");
        }

        // Calculate booking cost
        BigDecimal totalCost = workspace.getHourlyRate();

        if (totalCost == null || user.getWalletBalance() == null) {
            throw new IllegalStateException(
                    "Workspace rate or user wallet balance is missing!");
        }

        // Check wallet balance
        if (user.getWalletBalance().compareTo(totalCost) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient wallet balance!");
        }

        // Deduct wallet balance
        BigDecimal newBalance =
                user.getWalletBalance().subtract(totalCost);

        userDao.updateWalletBalance(
                user.getUserId(),
                newBalance
        );

        // Save booking details
        booking.setTotalCost(totalCost);
        booking.setBookingStatus("CONFIRMED");

        try {
            bookingDao.addBooking(booking);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Booking could not be completed. "
                    + "The slot may already be booked.", e);
        }
    }

    // Cancel booking and refund amount
    @Transactional
    public void cancelBooking(Long bookingId) {

        if (bookingId == null) {
            throw new IllegalArgumentException(
                    "Booking ID is required!");
        }

        Booking booking = bookingDao.getBookingById(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking not found!");
        }

        if (!"CONFIRMED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException(
                    "Booking is already cancelled!");
        }

        if (booking.getBookingDate() == null ||
                !booking.getBookingDate().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Only future bookings can be cancelled!");
        }

        int updatedRows =
                bookingDao.cancelConfirmedBooking(bookingId);

        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "Booking is already cancelled!");
        }

        // Find user before refunding
        User user = userDao.getUserById(booking.getUserId());

        if (user == null) {
            throw new IllegalStateException(
                    "User associated with this booking was not found!");
        }

        if (user.getWalletBalance() == null ||
                booking.getTotalCost() == null) {

            throw new IllegalStateException(
                    "Wallet balance or booking amount is missing!");
        }

        // Refund booking amount
        BigDecimal refundAmount =
                user.getWalletBalance().add(booking.getTotalCost());

        userDao.updateWalletBalance(
                user.getUserId(),
                refundAmount
        );
    }
}
