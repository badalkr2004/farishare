package com.fairsharebu.dao;

import com.fairsharebu.model.User;

import java.sql.SQLException;

/**
 * DAO interface for User entity. Extends the generic DAO interface.
 */
public interface UserDAO extends DAO<User, Integer> {

    /**
     * Find a user by username.
     * 
     * @param username The username to search for
     * @return The user with the given username, or null if not found
     * @throws SQLException If a database error occurs
     */
    User findByUsername(String username) throws SQLException;

    /**
     * Find a user by email.
     * 
     * @param email The email to search for
     * @return The user with the given email, or null if not found
     * @throws SQLException If a database error occurs
     */
    User findByEmail(String email) throws SQLException;

    /**
     * Authenticate a user by username and password.
     * 
     * @param username The username
     * @param password The password (unhashed)
     * @return The authenticated user, or null if authentication fails
     * @throws SQLException If a database error occurs
     */
    User authenticate(String username, String password) throws SQLException;

    /**
     * Change a user's password.
     * 
     * @param userId      The ID of the user
     * @param newPassword The new password (unhashed)
     * @return true if the password was changed successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean changePassword(int userId, String newPassword) throws SQLException;

    /**
     * Update a user's profile information (fullName, phoneNumber, and
     * profilePicture).
     * 
     * @param user The user with updated information
     * @return true if the profile was updated successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateProfile(User user) throws SQLException;
}