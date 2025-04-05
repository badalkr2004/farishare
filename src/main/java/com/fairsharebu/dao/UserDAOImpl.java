package com.fairsharebu.dao;

import com.fairsharebu.model.User;
import com.fairsharebu.util.DatabaseUtil;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the UserDAO interface.
 */
public class UserDAOImpl implements UserDAO {

    /**
     * Insert a new user into the database.
     * The password will be hashed before storing.
     */
    @Override
    public User insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, fullName, phoneNumber, profilePicture, registrationDate, isActive) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Hash the password before storing
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashedPassword);
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getProfilePicture());
            stmt.setTimestamp(7, Timestamp.valueOf(user.getRegistrationDate()));
            stmt.setBoolean(8, user.isActive());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            return user;
        }
    }

    /**
     * Update an existing user in the database.
     */
    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, fullName = ?, phoneNumber = ?, " +
                "profilePicture = ?, isActive = ? WHERE userId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getProfilePicture());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Delete a user from the database by ID.
     */
    @Override
    public boolean delete(Integer userId) throws SQLException {
        String sql = "DELETE FROM users WHERE userId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get a user by ID.
     */
    @Override
    public User get(Integer userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Get all users from the database.
     */
    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }

            return users;
        }
    }

    /**
     * Find a user by username.
     */
    @Override
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Find a user by email.
     */
    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Authenticate a user by username and password.
     */
    @Override
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = extractUserFromResultSet(rs);
                    String storedPassword = user.getPassword();

                    // Check if the provided password matches the stored hash
                    if (BCrypt.checkpw(password, storedPassword)) {
                        return user;
                    }
                }
                return null;
            }
        }
    }

    /**
     * Change a user's password.
     */
    @Override
    public boolean changePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE userId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Update a user's profile information.
     */
    @Override
    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET fullName = ?, phoneNumber = ?, profilePicture = ? WHERE userId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getProfilePicture());
            stmt.setInt(4, user.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Extract a User object from a ResultSet.
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("userId"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("fullName"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setProfilePicture(rs.getString("profilePicture"));

        Timestamp registrationDate = rs.getTimestamp("registrationDate");
        if (registrationDate != null) {
            user.setRegistrationDate(registrationDate.toLocalDateTime());
        }

        user.setActive(rs.getBoolean("isActive"));

        return user;
    }
}