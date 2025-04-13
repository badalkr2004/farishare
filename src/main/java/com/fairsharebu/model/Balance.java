package com.fairsharebu.model;

/**
 * Represents a balance for a user in a group
 */
public class Balance {
    private User user;
    private double amount;

    public Balance(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}