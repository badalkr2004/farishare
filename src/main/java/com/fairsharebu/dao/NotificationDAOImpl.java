package com.fairsharebu.dao;

import com.fairsharebu.model.Notification;
import com.fairsharebu.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {
    private final Connection connection;

    public NotificationDAOImpl() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }

    @Override
    public List<Notification> getNotificationsForUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE userId = ? ORDER BY timestamp DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        }
        return notifications;
    }

    @Override
    public void createNotification(int userId, String title, String message, String link) throws SQLException {
        String sql = "INSERT INTO notifications (userId, title, message, link, timestamp, isRead) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setString(4, link);
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setBoolean(6, false);
            stmt.executeUpdate();
        }
    }

    @Override
    public void markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET isRead = true WHERE notificationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET isRead = true WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteNotification(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notificationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        }
    }

    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notificationId"));
        notification.setUserId(rs.getInt("userId"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setLink(rs.getString("link"));
        notification.setCreatedAt(rs.getTimestamp("timestamp"));
        notification.setRead(rs.getBoolean("isRead"));
        return notification;
    }
}