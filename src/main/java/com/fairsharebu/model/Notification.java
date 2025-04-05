package com.fairsharebu.model;

import java.time.LocalDateTime;

/**
 * Represents a notification in the FairShareBU system.
 */
public class Notification {
    private int notificationId;
    private User recipient;
    private String type; // "EXPENSE_ADDED", "EXPENSE_SETTLED", "GROUP_INVITATION", "PAYMENT_REMINDER"
    private String message;
    private String link; // Link to redirect when clicked on the notification
    private LocalDateTime timestamp;
    private boolean isRead;

    // Default constructor
    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    // Parameterized constructor
    public Notification(User recipient, String type, String message, String link) {
        this();
        this.recipient = recipient;
        this.type = type;
        this.message = message;
        this.link = link;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Factory method to create an expense added notification.
     * 
     * @param recipient The user who will receive the notification
     * @param expense   The expense that was added
     * @return A new notification
     */
    public static Notification createExpenseAddedNotification(User recipient, Expense expense) {
        String message = expense.getPaidBy().getFullName() + " added an expense \"" +
                expense.getDescription() + "\" of ₹" + expense.getAmount() +
                " in group " + expense.getGroup().getName();

        String link = "/expense?id=" + expense.getExpenseId();

        return new Notification(recipient, "EXPENSE_ADDED", message, link);
    }

    /**
     * Factory method to create a payment reminder notification.
     * 
     * @param recipient The user who will receive the notification
     * @param expense   The expense for which payment is due
     * @return A new notification
     */
    public static Notification createPaymentReminderNotification(User recipient, Expense expense) {
        double share = expense.getShare(recipient);

        String message = "You owe ₹" + share + " to " + expense.getPaidBy().getFullName() +
                " for \"" + expense.getDescription() + "\" in group " + expense.getGroup().getName();

        String link = "/expense?id=" + expense.getExpenseId();

        return new Notification(recipient, "PAYMENT_REMINDER", message, link);
    }

    /**
     * Factory method to create a group invitation notification.
     * 
     * @param recipient The user who will receive the notification
     * @param group     The group the user is invited to
     * @param inviter   The user who sent the invitation
     * @return A new notification
     */
    public static Notification createGroupInvitationNotification(User recipient, Group group, User inviter) {
        String message = inviter.getFullName() + " has invited you to join the group \"" +
                group.getName() + "\"";

        String link = "/group?id=" + group.getGroupId();

        return new Notification(recipient, "GROUP_INVITATION", message, link);
    }

    /**
     * Mark the notification as read.
     */
    public void markAsRead() {
        this.isRead = true;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", recipient=" + (recipient != null ? recipient.getUsername() : "null") +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}