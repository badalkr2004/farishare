package com.fairsharebu.dao;

import com.fairsharebu.model.Expense;
import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;
import com.fairsharebu.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the ExpenseDAO interface for expense-related database
 * operations
 */
public class ExpenseDAOImpl implements ExpenseDAO {

    private UserDAO userDAO;
    private GroupDAO groupDAO;

    public ExpenseDAOImpl() throws SQLException {
        this.userDAO = new UserDAOImpl();
        this.groupDAO = new GroupDAOImpl();
    }

    @Override
    public Expense getExpenseById(int expenseId) throws SQLException {
        String query = "SELECT * FROM expenses WHERE expenseId = ?";
        Expense expense = null;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, expenseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    expense = extractExpenseFromResultSet(rs);

                    // Load participants and their shares
                    loadExpenseParticipants(expense, conn);
                }
            }
        }

        return expense;
    }

    @Override
    public List<Expense> getExpensesByGroup(int groupId) throws SQLException {
        String query = "SELECT * FROM expenses WHERE group_id = ? ORDER BY created_at DESC";
        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Expense expense = extractExpenseFromResultSet(rs);
                    loadExpenseParticipants(expense, conn);
                    expenses.add(expense);
                }
            }
        }

        return expenses;
    }

    @Override
    public List<Expense> getExpensesByUser(int userId) throws SQLException {
        String query = "SELECT * FROM expenses WHERE paid_by = ? ORDER BY created_at DESC";
        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Expense expense = extractExpenseFromResultSet(rs);
                    loadExpenseParticipants(expense, conn);
                    expenses.add(expense);
                }
            }
        }

        return expenses;
    }

    @Override
    public void addExpense(Expense expense) throws SQLException {
        String query = "INSERT INTO expenses (group_id, paid_by, amount, description, created_at, " +
                "receipt_image, payment_method, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, expense.getGroup().getGroupId());
                stmt.setInt(2, expense.getPaidBy().getUserId());
                stmt.setDouble(3, expense.getAmount());
                stmt.setString(4, expense.getDescription());
                stmt.setTimestamp(5, Timestamp.valueOf(expense.getCreatedAt()));
                stmt.setString(6, expense.getReceiptImage());
                stmt.setString(7, expense.getPaymentMethod());
                stmt.setString(8, expense.getStatus());

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int expenseId = generatedKeys.getInt(1);
                        expense.setExpenseId(expenseId);

                        // Add expense participants
                        saveExpenseParticipants(expense, conn);

                        // Commit transaction
                        conn.commit();
                    } else {
                        throw new SQLException("Creating expense failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error with SQL query: " + e.getMessage());

            if (conn != null) {
                try {
                    conn.rollback(); // Rollback the transaction
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void updateExpense(Expense expense) throws SQLException {
        String query = "UPDATE expenses SET group_id = ?, paid_by = ?, amount = ?, description = ?, " +
                "receipt_image = ?, payment_method = ?, status = ? WHERE expenseId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, expense.getGroup().getGroupId());
            stmt.setInt(2, expense.getPaidBy().getUserId());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getDescription());
            stmt.setString(5, expense.getReceiptImage());
            stmt.setString(6, expense.getPaymentMethod());
            stmt.setString(7, expense.getStatus());
            stmt.setInt(8, expense.getExpenseId());

            stmt.executeUpdate();

            // Update participants and shares
            // First delete existing participants
            deleteExpenseParticipants(expense.getExpenseId(), conn);

            // Then save the updated participants
            saveExpenseParticipants(expense, conn);
        }
    }

    @Override
    public void deleteExpense(int expenseId) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // First delete participants
            deleteExpenseParticipants(expenseId, conn);

            // Then delete the expense
            String query = "DELETE FROM expenses WHERE expenseId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, expenseId);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public double getAmountOwedByUser(int groupId, int userId) throws SQLException {
        String query = "SELECT SUM(ep.share_amount) as total_owed " +
                "FROM expense_participants ep " +
                "JOIN expenses e ON ep.expense_id = e.expenseId " +
                "WHERE e.group_id = ? AND ep.user_id = ? AND e.paid_by != ? AND e.status = 'PENDING'";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble("total_owed");
                    return amount;
                }
            }
        }

        return 0.0;
    }

    @Override
    public double getAmountOwedToUser(int groupId, int userId) throws SQLException {
        String query = "SELECT SUM(ep.share_amount) as total_to_receive " +
                "FROM expense_participants ep " +
                "JOIN expenses e ON ep.expense_id = e.expenseId " +
                "WHERE e.group_id = ? AND e.paid_by = ? AND ep.user_id != ? AND e.status = 'PENDING'";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble("total_to_receive");
                    return amount;
                }
            }
        }

        return 0.0;
    }

    @Override
    public List<Expense> getRecentExpensesByGroup(int groupId, int limit) throws SQLException {
        String query = "SELECT * FROM expenses WHERE group_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Expense expense = extractExpenseFromResultSet(rs);
                    loadExpenseParticipants(expense, conn);
                    expenses.add(expense);
                }
            }
        }

        return expenses;
    }

    @Override
    public Map<Integer, Double> getExpenseParticipants(int expenseId) throws SQLException {
        String query = "SELECT user_id, share_amount FROM expense_participants WHERE expense_id = ?";
        Map<Integer, Double> participants = new java.util.HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, expenseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    double shareAmount = rs.getDouble("share_amount");
                    participants.put(userId, shareAmount);
                }
            }
        }

        return participants;
    }

    @Override
    public void addParticipantToExpense(int expenseId, int userId, double share) throws SQLException {
        String query = "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, expenseId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, share);

            stmt.executeUpdate();
        }
    }

    @Override
    public void updateParticipantShare(int expenseId, int userId, double share) throws SQLException {
        String query = "UPDATE expense_participants SET share_amount = ? WHERE expense_id = ? AND user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, share);
            stmt.setInt(2, expenseId);
            stmt.setInt(3, userId);

            stmt.executeUpdate();
        }
    }

    @Override
    public void updateParticipantPaidStatus(int expenseId, int userId, boolean isPaid) throws SQLException {
        String query = "UPDATE expense_participants SET is_paid = ? WHERE expense_id = ? AND user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, isPaid);
            stmt.setInt(2, expenseId);
            stmt.setInt(3, userId);

            stmt.executeUpdate();
        }
    }

    private Expense extractExpenseFromResultSet(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setExpenseId(rs.getInt("expenseId"));
        expense.setDescription(rs.getString("description"));
        expense.setAmount(rs.getDouble("amount"));
        expense.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        expense.setReceiptImage(rs.getString("receipt_image"));
        expense.setPaymentMethod(rs.getString("payment_method"));
        expense.setStatus(rs.getString("status"));

        // Load related objects
        User paidBy = userDAO.get(rs.getInt("paid_by"));
        Group group = groupDAO.getGroupById(rs.getInt("group_id"));

        expense.setPaidBy(paidBy);
        expense.setGroup(group);

        return expense;
    }

    private void loadExpenseParticipants(Expense expense, Connection conn) throws SQLException {
        String query = "SELECT user_id, share_amount FROM expense_participants WHERE expense_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, expense.getExpenseId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User participant = userDAO.get(rs.getInt("user_id"));
                    double shareAmount = rs.getDouble("share_amount");

                    expense.addParticipant(participant);
                    expense.setShare(participant, shareAmount);
                }
            }
        }
    }

    private void saveExpenseParticipants(Expense expense, Connection conn) throws SQLException {
        String query = "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Map.Entry<User, Double> entry : expense.getShares().entrySet()) {
                User participant = entry.getKey();
                Double shareAmount = entry.getValue();

                stmt.setInt(1, expense.getExpenseId());
                stmt.setInt(2, participant.getUserId());
                stmt.setDouble(3, shareAmount);

                stmt.executeUpdate();
            }
        }
    }

    private void deleteExpenseParticipants(int expenseId, Connection conn) throws SQLException {
        String query = "DELETE FROM expense_participants WHERE expense_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, expenseId);
            stmt.executeUpdate();
        }
    }
}