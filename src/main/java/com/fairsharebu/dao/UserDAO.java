package com.fairsharebu.dao;

import com.fairsharebu.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for User-related database operations
 */
public interface UserDAO extends DAO<User, Integer> {

    /**
     * Finds a user by username
     *
     * @param username The username to search for
     * @return The user with the specified username, or null if not found
     * @throws SQLException If a database error occurs
     */
    User findByUsername(String username) throws SQLException;

    /**
     * Finds a user by email address
     *
     * @param email The email to search for
     * @return The user with the specified email, or null if not found
     * @throws SQLException If a database error occurs
     */
    User findByEmail(String email) throws SQLException;

    /**
     * Registers a new user
     *
     * @param user The user to register
     * @return The registered user with the ID set
     * @throws SQLException If a database error occurs
     */
    User register(User user) throws SQLException;

    /**
     * Authenticates a user
     *
     * @param username The username
     * @param password The password (plain text)
     * @return The authenticated user, or null if authentication fails
     * @throws SQLException If a database error occurs
     */
    User authenticate(String username, String password) throws SQLException;

    /**
     * Updates a user's password
     *
     * @param userId      The ID of the user
     * @param newPassword The new password (plain text)
     * @return true if the password was updated successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updatePassword(int userId, String newPassword) throws SQLException;

    /**
     * Updates a user's profile
     *
     * @param user The user with updated information
     * @return true if the profile was updated successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateProfile(User user) throws SQLException;

}