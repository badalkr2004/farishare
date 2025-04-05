package com.fairsharebu.dao;

import com.fairsharebu.model.User;
import com.fairsharebu.util.DatabaseUtil;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the UserDAO interface for user-related database operations
 */
public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    public UserDAOImpl() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }

    /**
     * Insert a new user into the database.
     * The password will be hashed before storing.
     */
    @Override
    public User insert(User user) throws SQLException {
        return register(user);
    }

    /**
     * Update an existing user in the database.
     */
    @Override
    public boolean update(User user) throws SQLException {
        return updateProfile(user);
    }

    /**
     * Delete a user from the database by ID.
     */
    @Override
    public boolean delete(Integer userId) throws SQLException {
        String query = "DELETE FROM users WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
        String query = "SELECT * FROM users WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Get all users from the database.
     */
    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY fullName";

        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }

        return users;
    }

    /**
     * Find a user by username.
     */
    @Override
    public User findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Find a user by email.
     */
    @Override
    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Authenticate a user by username and password.
     */
    @Override
    public User authenticate(String username, String password) throws SQLException {
        User user = findByUsername(username);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    /**
     * Change a user's password.
     */
    @Override
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
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
        String query = "UPDATE users SET fullName = ?, phoneNumber = ?, profilePicture = ? WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("fullName"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setProfilePicture(rs.getString("profilePicture"));

        // Convert java.sql.Timestamp to LocalDateTime
        Timestamp timestamp = rs.getTimestamp("registrationDate");
        if (timestamp != null) {
            user.setRegistrationDate(timestamp.toLocalDateTime());
        }

        user.setActive(rs.getBoolean("isActive"));

        return user;
    }

    @Override
    public User register(User user) throws SQLException {
        // Check if username or email already exists
        if (findByUsername(user.getUsername()) != null) {
            throw new SQLException("Username already exists");
        }

        if (findByEmail(user.getEmail()) != null) {
            throw new SQLException("Email already exists");
        }

        String query = "INSERT INTO users (username, password, email, fullName, phoneNumber, profilePicture, registrationDate, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getProfilePicture());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(8, true);

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
        }

        return user;
    }
}