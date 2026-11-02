package com.coworking.hotdesk.rowmapper;

import com.coworking.hotdesk.entity.Booking;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingRowMapper implements RowMapper<Booking> {

    @Override
    public Booking mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Booking booking = new Booking();

        booking.setBookingId(rs.getLong("booking_id"));
        booking.setUserId(rs.getLong("user_id"));
        booking.setSpaceId(rs.getLong("space_id"));
        booking.setBookingDate(
                rs.getDate("booking_date").toLocalDate()
        );
        booking.setSlotHour(rs.getInt("slot_hour"));
        booking.setTotalCost(rs.getBigDecimal("total_cost"));
        booking.setBookingStatus(rs.getString("booking_status"));

        return booking;
    }
}