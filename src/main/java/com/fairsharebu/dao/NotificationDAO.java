package com.fairsharebu.dao;

import com.fairsharebu.model.Notification;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Notification-related database operations
 */
public interface NotificationDAO {

    /**
     * Gets all notifications for a specific user
     * 
     * @param userId The ID of the user
     * @return A list of notifications for the user
     * @throws SQLException If a database error occurs
     */
    List<Notification> getNotificationsForUser(int userId) throws SQLException;

    /**
     * Creates a new notification in the database
     * 
     * @param userId  The ID of the user
     * @param title   The title of the notification
     * @param message The message of the notification
     * @param link    The link associated with the notification
     * @throws SQLException If a database error occurs
     */
    void createNotification(int userId, String title, String message, String link) throws SQLException;

    /**
     * Marks a notification as read
     * 
     * @param notificationId The ID of the notification
     * @throws SQLException If a database error occurs
     */
    void markAsRead(int notificationId) throws SQLException;

    /**
     * Marks all notifications of a user as read
     * 
     * @param userId The ID of the user
     * @throws SQLException If a database error occurs
     */
    void markAllAsRead(int userId) throws SQLException;

    /**
     * Deletes a notification from the database
     * 
     * @param notificationId The ID of the notification to be deleted
     * @throws SQLException If a database error occurs
     */
    void deleteNotification(int notificationId) throws SQLException;
}