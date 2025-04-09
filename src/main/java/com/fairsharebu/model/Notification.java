package com.fairsharebu.model;

import java.util.Date;

/**
 * Represents a notification in the FairShareBU system.
 */
public class Notification {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private String link; // Link to redirect when clicked on the notification
    private Date createdAt;
    private boolean isRead;

    // Default constructor
    public Notification() {
        this.createdAt = new Date();
        this.isRead = false;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

        Notification notification = new Notification();
        notification.setUserId(recipient.getUserId());
        notification.setMessage(message);
        notification.setLink(link);
        notification.setRead(false);

        return notification;
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

        Notification notification = new Notification();
        notification.setUserId(recipient.getUserId());
        notification.setMessage(message);
        notification.setLink(link);
        notification.setRead(false);

        return notification;
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

        Notification notification = new Notification();
        notification.setUserId(recipient.getUserId());
        notification.setMessage(message);
        notification.setLink(link);
        notification.setRead(false);

        return notification;
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
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", link='" + link + '\'' +
                ", createdAt=" + createdAt +
                ", isRead=" + isRead +
                '}';
    }
}