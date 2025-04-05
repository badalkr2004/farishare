package com.fairsharebu.dao;

import com.fairsharebu.model.Notification;
import com.fairsharebu.model.User;
import com.fairsharebu.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the NotificationDAO interface for notification-related
 * database operations
 */
public class NotificationDAOImpl implements NotificationDAO {

    private final Connection connection;
    private final UserDAO userDAO;
    private final GroupDAO groupDAO;

    public NotificationDAOImpl() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
        this.userDAO = new UserDAOImpl();
        this.groupDAO = new GroupDAOImpl();
    }

    @Override
    public Notification insert(Notification notification) throws SQLException {
        return createNotification(notification);
    }

    @Override
    public boolean update(Notification notification) throws SQLException {
        return updateNotification(notification);
    }

    @Override
    public boolean delete(Integer notificationId) throws SQLException {
        return deleteNotification(notificationId);
    }

    @Override
    public Notification get(Integer notificationId) throws SQLException {
        return getNotificationById(notificationId);
    }

    @Override
    public List<Notification> getAll() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications";

        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        }

        return notifications;
    }

    @Override
    public Notification createNotification(Notification notification) throws SQLException {
        String query = "INSERT INTO notifications (recipientId, type, message, link, isRead) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notification.getRecipient().getUserId());
            stmt.setString(2, notification.getType());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getLink());
            stmt.setBoolean(5, notification.isRead());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating notification failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setNotificationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating notification failed, no ID obtained.");
                }
            }
        }

        return notification;
    }

    @Override
    public boolean updateNotification(Notification notification) throws SQLException {
        String query = "UPDATE notifications SET type = ?, message = ?, link = ?, isRead = ? WHERE notificationId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, notification.getType());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getLink());
            stmt.setBoolean(4, notification.isRead());
            stmt.setInt(5, notification.getNotificationId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteNotification(int notificationId) throws SQLException {
        String query = "DELETE FROM notifications WHERE notificationId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public Notification getNotificationById(int notificationId) throws SQLException {
        String query = "SELECT * FROM notifications WHERE notificationId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNotificationFromResultSet(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Notification> getNotificationsByUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE recipientId = ? ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(extractNotificationFromResultSet(rs));
                }
            }
        }

        return notifications;
    }

    @Override
    public List<Notification> getUnreadNotificationsByUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE recipientId = ? AND isRead = FALSE ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(extractNotificationFromResultSet(rs));
                }
            }
        }

        return notifications;
    }

    @Override
    public boolean markNotificationAsRead(int notificationId) throws SQLException {
        String query = "UPDATE notifications SET isRead = TRUE WHERE notificationId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean markAllNotificationsAsRead(int userId) throws SQLException {
        String query = "UPDATE notifications SET isRead = TRUE WHERE recipientId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean createExpenseNotification(int expenseId, String message) throws SQLException {
        // Get the expense details to create notifications for all participants
        String getExpenseQuery = "SELECT e.*, g.name as groupName FROM expenses e " +
                "JOIN groups g ON e.groupId = g.groupId " +
                "WHERE e.expenseId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(getExpenseQuery)) {
            stmt.setInt(1, expenseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int groupId = rs.getInt("groupId");
                    int paidById = rs.getInt("paidById");
                    String expenseName = rs.getString("description");
                    String groupName = rs.getString("groupName");

                    // Get the user who paid
                    User paidByUser = userDAO.get(paidById);

                    // Create a message if none was provided
                    if (message == null || message.isEmpty()) {
                        message = paidByUser.getFullName() + " added an expense \"" + expenseName + "\" in group "
                                + groupName;
                    }

                    // Create a link to the expense
                    String link = "/expense?id=" + expenseId;

                    // Send notifications to all group members except the one who paid
                    return createGroupNotification(groupId, message, link, paidById);
                }
            }
        }

        return false;
    }

    @Override
    public boolean createGroupNotification(int groupId, String message, String link, int excludeUserId)
            throws SQLException {
        // Get all members of the group
        List<User> groupMembers = groupDAO.getGroupMembers(groupId);

        // Create a notification for each member except the excluded user
        for (User member : groupMembers) {
            if (member.getUserId() != excludeUserId) {
                Notification notification = new Notification();
                notification.setRecipient(member);
                notification.setType("GROUP_NOTIFICATION");
                notification.setMessage(message);
                notification.setLink(link);
                notification.setRead(false);

                createNotification(notification);
            }
        }

        return true;
    }

    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notificationId"));

        // Get the recipient user
        int recipientId = rs.getInt("recipientId");
        User recipient = userDAO.get(recipientId);
        notification.setRecipient(recipient);

        notification.setType(rs.getString("type"));
        notification.setMessage(rs.getString("message"));
        notification.setLink(rs.getString("link"));

        // Convert java.sql.Timestamp to LocalDateTime
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            notification.setTimestamp(timestamp.toLocalDateTime());
        }

        notification.setRead(rs.getBoolean("isRead"));

        return notification;
    }
}