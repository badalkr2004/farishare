package com.fairsharebu.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an expense in the FairShareBU system.
 * An expense is created by a user and can be split among multiple users.
 */
public class Expense {
    private int expenseId;
    private String description;
    private double amount;
    private User paidBy;
    private Group group;
    private LocalDateTime date;
    private String receiptImage;
    private String paymentMethod; // e.g., "Cash", "UPI", "Card"
    private String status; // "PENDING", "SETTLED"

    // Participants and their shares
    private List<User> participants;
    private Map<User, Double> shares;

    // Default constructor
    public Expense() {
        this.date = LocalDateTime.now();
        this.participants = new ArrayList<>();
        this.shares = new HashMap<>();
        this.status = "PENDING";
    }

    // Parameterized constructor
    public Expense(String description, double amount, User paidBy, Group group, String paymentMethod) {
        this();
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.group = group;
        this.paymentMethod = paymentMethod;

        // Add the expense to the user who paid
        paidBy.addExpenseCreated(this);

        // Add the expense to the group
        if (group != null) {
            group.addExpense(this);
        }
    }

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getReceiptImage() {
        return receiptImage;
    }

    public void setReceiptImage(String receiptImage) {
        this.receiptImage = receiptImage;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public Map<User, Double> getShares() {
        return shares;
    }

    public void setShares(Map<User, Double> shares) {
        this.shares = shares;
    }

    // Helper methods
    public void addParticipant(User user) {
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }
        if (!participants.contains(user)) {
            this.participants.add(user);
            user.addExpenseParticipated(this);
        }
    }

    public void addParticipants(List<User> users) {
        for (User user : users) {
            addParticipant(user);
        }
    }

    public void setShare(User user, double share) {
        if (this.shares == null) {
            this.shares = new HashMap<>();
        }
        // Make sure the user is a participant
        if (!participants.contains(user)) {
            addParticipant(user);
        }
        this.shares.put(user, share);
    }

    public double getShare(User user) {
        if (this.shares == null || !this.shares.containsKey(user)) {
            return 0.0;
        }
        return this.shares.get(user);
    }

    /**
     * Split the expense equally among all participants.
     */
    public void splitEqually() {
        if (participants == null || participants.isEmpty()) {
            return;
        }

        double shareAmount = amount / participants.size();
        for (User participant : participants) {
            setShare(participant, shareAmount);
        }
    }

    /**
     * Split the expense by percentage.
     * 
     * @param percentages Map of users to their percentage of the bill
     */
    public void splitByPercentage(Map<User, Double> percentages) {
        for (Map.Entry<User, Double> entry : percentages.entrySet()) {
            User user = entry.getKey();
            Double percentage = entry.getValue();
            double share = (percentage / 100.0) * amount;
            setShare(user, share);
        }
    }

    /**
     * Split the expense by specific amounts.
     * 
     * @param specificAmounts Map of users to their specific amount
     */
    public void splitByAmount(Map<User, Double> specificAmounts) {
        for (Map.Entry<User, Double> entry : specificAmounts.entrySet()) {
            setShare(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Mark the expense as settled.
     */
    public void settle() {
        this.status = "SETTLED";
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", paidBy=" + (paidBy != null ? paidBy.getUsername() : "null") +
                ", group=" + (group != null ? group.getName() : "null") +
                ", date=" + date +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", participantCount=" + (participants != null ? participants.size() : 0) +
                '}';
    }
}