
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
            throw new IllegalArgumentException("Booking details are required!");
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

        User user = userDao.getUserById(booking.getUserId());

        Workspace workspace =
                workspaceDao.getWorkspaceById(booking.getSpaceId());

        if (!workspace.isActive()) {
            throw new IllegalArgumentException(
                    "Workspace is not active!");
        }

        boolean available = bookingDao.isSlotAvailable(
                booking.getSpaceId(),
                booking.getBookingDate(),
                booking.getSlotHour()
        );

        if (!available) {
            throw new IllegalArgumentException(
                    "This workspace slot is already booked!");
        }

        BigDecimal totalCost = workspace.getHourlyRate();

        if (user.getWalletBalance().compareTo(totalCost) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient wallet balance!");
        }

        BigDecimal newBalance =
                user.getWalletBalance().subtract(totalCost);

        userDao.updateWalletBalance(
                user.getUserId(),
                newBalance
        );

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

        Booking booking = bookingDao.getBookingById(bookingId);

        if (!"CONFIRMED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException(
                    "Booking is already cancelled!");
        }

        int updatedRows =
                bookingDao.cancelConfirmedBooking(bookingId);

        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "Booking is already cancelled!");
        }

        User user = userDao.getUserById(booking.getUserId());

        BigDecimal refundAmount =
                user.getWalletBalance().add(booking.getTotalCost());

        userDao.updateWalletBalance(
                user.getUserId(),
                refundAmount
        );
    }
}
