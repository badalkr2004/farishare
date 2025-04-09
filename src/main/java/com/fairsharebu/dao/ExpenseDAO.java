package com.fairsharebu.dao;

import com.fairsharebu.model.Expense;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface for Expense-related database operations
 */
public interface ExpenseDAO {
    /**
     * Get an expense by its ID
     */
    Expense getExpenseById(int expenseId) throws SQLException;

    /**
     * Add a new expense
     */
    void addExpense(Expense expense) throws SQLException;

    /**
     * Update an existing expense
     */
    void updateExpense(Expense expense) throws SQLException;

    /**
     * Delete an expense
     */
    void deleteExpense(int expenseId) throws SQLException;

    /**
     * Get all expenses for a user
     */
    List<Expense> getExpensesByUser(int userId) throws SQLException;

    /**
     * Get all expenses for a group
     */
    List<Expense> getExpensesByGroup(int groupId) throws SQLException;

    /**
     * Get recent expenses for a group with limit
     */
    List<Expense> getRecentExpensesByGroup(int groupId, int limit) throws SQLException;

    /**
     * Calculate amount owed by a user in a group
     */
    double getAmountOwedByUser(int groupId, int userId) throws SQLException;

    /**
     * Calculate amount owed to a user in a group
     */
    double getAmountOwedToUser(int groupId, int userId) throws SQLException;

    /**
     * Add a participant to an expense with their share
     */
    void addParticipantToExpense(int expenseId, int userId, double share) throws SQLException;

    /**
     * Update a participant's share in an expense
     */
    void updateParticipantShare(int expenseId, int userId, double share) throws SQLException;

    /**
     * Update whether a participant has paid their share
     */
    void updateParticipantPaidStatus(int expenseId, int userId, boolean isPaid) throws SQLException;

    /**
     * Get all participants and their shares for an expense
     */
    Map<Integer, Double> getExpenseParticipants(int expenseId) throws SQLException;
}