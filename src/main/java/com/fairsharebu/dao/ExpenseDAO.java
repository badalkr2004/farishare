package com.fairsharebu.dao;

import com.fairsharebu.model.Expense;
import com.fairsharebu.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface for Expense-related database operations
 */
public interface ExpenseDAO extends DAO<Expense, Integer> {

    /**
     * Creates a new expense in the database
     * 
     * @param expense The expense to be created
     * @return The created expense with the ID set
     * @throws SQLException If a database error occurs
     */
    Expense createExpense(Expense expense) throws SQLException;

    /**
     * Updates an existing expense in the database
     * 
     * @param expense The expense to be updated
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateExpense(Expense expense) throws SQLException;

    /**
     * Deletes an expense from the database
     * 
     * @param expenseId The ID of the expense to be deleted
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean deleteExpense(int expenseId) throws SQLException;

    /**
     * Gets an expense by its ID
     * 
     * @param expenseId The ID of the expense to be retrieved
     * @return The expense with the specified ID, or null if not found
     * @throws SQLException If a database error occurs
     */
    Expense getExpenseById(int expenseId) throws SQLException;

    /**
     * Gets all expenses for a specific group
     * 
     * @param groupId The ID of the group
     * @return A list of expenses for the group
     * @throws SQLException If a database error occurs
     */
    List<Expense> getExpensesByGroup(int groupId) throws SQLException;

    /**
     * Gets all expenses paid by a specific user
     * 
     * @param userId The ID of the user
     * @return A list of expenses paid by the user
     * @throws SQLException If a database error occurs
     */
    List<Expense> getExpensesPaidByUser(int userId) throws SQLException;

    /**
     * Gets all expenses where a specific user is a participant
     * 
     * @param userId The ID of the user
     * @return A list of expenses where the user is a participant
     * @throws SQLException If a database error occurs
     */
    List<Expense> getExpensesWhereUserParticipated(int userId) throws SQLException;

    /**
     * Gets all participants of an expense
     * 
     * @param expenseId The ID of the expense
     * @return A map of users and their shares in the expense
     * @throws SQLException If a database error occurs
     */
    Map<User, Double> getExpenseParticipants(int expenseId) throws SQLException;

    /**
     * Adds a participant to an expense with a specific share
     * 
     * @param expenseId The ID of the expense
     * @param userId    The ID of the user
     * @param share     The share amount of the user
     * @return true if the participant was added successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean addParticipantToExpense(int expenseId, int userId, double share) throws SQLException;

    /**
     * Updates a participant's share in an expense
     * 
     * @param expenseId The ID of the expense
     * @param userId    The ID of the user
     * @param share     The new share amount of the user
     * @return true if the share was updated successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateParticipantShare(int expenseId, int userId, double share) throws SQLException;

    /**
     * Marks a participant's share as paid or unpaid
     * 
     * @param expenseId The ID of the expense
     * @param userId    The ID of the user
     * @param isPaid    Whether the share is paid or not
     * @return true if the share status was updated successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateParticipantPaidStatus(int expenseId, int userId, boolean isPaid) throws SQLException;

    /**
     * Calculates the total amount a user owes in a specific group
     * 
     * @param groupId The ID of the group
     * @param userId  The ID of the user
     * @return The total amount the user owes
     * @throws SQLException If a database error occurs
     */
    double calculateUserOwes(int groupId, int userId) throws SQLException;

    /**
     * Calculates the total amount owed to a user in a specific group
     * 
     * @param groupId The ID of the group
     * @param userId  The ID of the user
     * @return The total amount owed to the user
     * @throws SQLException If a database error occurs
     */
    double calculateUserIsOwed(int groupId, int userId) throws SQLException;
}