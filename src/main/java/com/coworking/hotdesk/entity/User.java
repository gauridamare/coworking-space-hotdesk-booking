package com.coworking.hotdesk.entity;

import java.math.BigDecimal;

public class User {

    private Long userId;
    private String fullName;
    private String email;
    private BigDecimal walletBalance;

    public User() {
    }

    public User(Long userId, String fullName,
                String email, BigDecimal walletBalance) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.walletBalance = walletBalance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
}