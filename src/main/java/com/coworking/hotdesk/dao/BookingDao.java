
package com.coworking.hotdesk.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.coworking.hotdesk.entity.Booking;
import com.coworking.hotdesk.rowmapper.BookingRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingDao {

    private final JdbcTemplate jdbcTemplate;

    public BookingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Booking> getAllBookings() {
        String sql = "SELECT * FROM bookings";
        return jdbcTemplate.query(sql, new BookingRowMapper());
    }

    public Booking getBookingById(Long bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        return jdbcTemplate.queryForObject(
                sql, new BookingRowMapper(), bookingId);
    }

    public int addBooking(Booking booking) {
        String sql = "INSERT INTO bookings "
                + "(user_id, space_id, booking_date, slot_hour, total_cost, booking_status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(
                sql,
                booking.getUserId(),
                booking.getSpaceId(),
                booking.getBookingDate(),
                booking.getSlotHour(),
                booking.getTotalCost(),
                booking.getBookingStatus());
    }

    public int cancelConfirmedBooking(Long bookingId) {
        String sql = "UPDATE bookings "
                + "SET booking_status = 'CANCELLED' "
                + "WHERE booking_id = ? "
                + "AND booking_status = 'CONFIRMED'";

        return jdbcTemplate.update(sql, bookingId);
    }

    public boolean isSlotAvailable(
            Long spaceId, LocalDate bookingDate, int slotHour) {

        String sql = "SELECT COUNT(*) FROM bookings "
                + "WHERE space_id = ? "
                + "AND booking_date = ? "
                + "AND slot_hour = ? "
                + "AND booking_status = 'CONFIRMED'";

        Integer count = jdbcTemplate.queryForObject(
                sql, Integer.class, spaceId, bookingDate, slotHour);

        return count != null && count == 0;
    }

    // Get revenue grouped by workspace type
    public List<Map<String, Object>> getRevenueBySpaceType() {

        String sql = "SELECT w.space_type, "
                + "SUM(b.total_cost) AS total_revenue "
                + "FROM bookings b "
                + "JOIN workspaces w ON b.space_id = w.space_id "
                + "WHERE b.booking_status = 'CONFIRMED' "
                + "GROUP BY w.space_type "
                + "ORDER BY w.space_type";

        return jdbcTemplate.queryForList(sql);
    }
}
