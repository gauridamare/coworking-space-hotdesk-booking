package com.coworking.hotdesk.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booking {

    private Long bookingId;
    private Long userId;
    private Long spaceId;
    private LocalDate bookingDate;
    private Integer slotHour;
    private BigDecimal totalCost;
    private String bookingStatus;

    public Booking() {
    }

    public Booking(Long bookingId, Long userId, Long spaceId,
                   LocalDate bookingDate, Integer slotHour,
                   BigDecimal totalCost, String bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.spaceId = spaceId;
        this.bookingDate = bookingDate;
        this.slotHour = slotHour;
        this.totalCost = totalCost;
        this.bookingStatus = bookingStatus;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getSlotHour() {
        return slotHour;
    }

    public void setSlotHour(Integer slotHour) {
        this.slotHour = slotHour;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}