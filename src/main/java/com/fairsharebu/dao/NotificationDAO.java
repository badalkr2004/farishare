package com.fairsharebu.dao;

import com.fairsharebu.model.Notification;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Notification-related database operations
 */
public interface NotificationDAO extends DAO<Notification, Integer> {

    /**
     * Creates a new notification in the database
     * 
     * @param notification The notification to be created
     * @return The created notification with the ID set
     * @throws SQLException If a database error occurs
     */
    Notification createNotification(Notification notification) throws SQLException;

    /**
     * Updates an existing notification in the database
     * 
     * @param notification The notification to be updated
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateNotification(Notification notification) throws SQLException;

    /**
     * Deletes a notification from the database
     * 
     * @param notificationId The ID of the notification to be deleted
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean deleteNotification(int notificationId) throws SQLException;

    /**
     * Gets a notification by its ID
     * 
     * @param notificationId The ID of the notification to be retrieved
     * @return The notification with the specified ID, or null if not found
     * @throws SQLException If a database error occurs
     */
    Notification getNotificationById(int notificationId) throws SQLException;

    /**
     * Gets all notifications for a specific user
     * 
     * @param userId The ID of the user
     * @return A list of notifications for the user
     * @throws SQLException If a database error occurs
     */
    List<Notification> getNotificationsByUser(int userId) throws SQLException;

    /**
     * Gets all unread notifications for a specific user
     * 
     * @param userId The ID of the user
     * @return A list of unread notifications for the user
     * @throws SQLException If a database error occurs
     */
    List<Notification> getUnreadNotificationsByUser(int userId) throws SQLException;

    /**
     * Marks a notification as read
     * 
     * @param notificationId The ID of the notification
     * @return true if the notification was marked as read successfully, false
     *         otherwise
     * @throws SQLException If a database error occurs
     */
    boolean markNotificationAsRead(int notificationId) throws SQLException;

    /**
     * Marks all notifications of a user as read
     * 
     * @param userId The ID of the user
     * @return true if the notifications were marked as read successfully, false
     *         otherwise
     * @throws SQLException If a database error occurs
     */
    boolean markAllNotificationsAsRead(int userId) throws SQLException;

    /**
     * Creates a notification for a specific expense
     * 
     * @param expenseId The ID of the expense
     * @param message   The notification message
     * @return true if the notification was created successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean createExpenseNotification(int expenseId, String message) throws SQLException;

    /**
     * Creates a notification for all members of a group
     * 
     * @param groupId       The ID of the group
     * @param message       The notification message
     * @param link          The link to include in the notification
     * @param excludeUserId The ID of the user to exclude from the notification
     *                      (usually the creator)
     * @return true if the notifications were created successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean createGroupNotification(int groupId, String message, String link, int excludeUserId) throws SQLException;
}