package com.fairsharebu.dao;

import com.fairsharebu.model.Expense;
import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;
import com.fairsharebu.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the ExpenseDAO interface for expense-related database
 * operations
 */
public class ExpenseDAOImpl implements ExpenseDAO {

    private final Connection connection;
    private final UserDAO userDAO;
    private final GroupDAO groupDAO;

    public ExpenseDAOImpl() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
        this.userDAO = new UserDAOImpl();
        this.groupDAO = new GroupDAOImpl();
    }

    @Override
    public Expense insert(Expense expense) throws SQLException {
        return createExpense(expense);
    }

    @Override
    public boolean update(Expense expense) throws SQLException {
        return updateExpense(expense);
    }

    @Override
    public boolean delete(Integer expenseId) throws SQLException {
        return deleteExpense(expenseId);
    }

    @Override
    public Expense get(Integer expenseId) throws SQLException {
        return getExpenseById(expenseId);
    }

    @Override
    public List<Expense> getAll() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses";

        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                expenses.add(extractExpenseFromResultSet(rs));
            }
        }

        return expenses;
    }

    @Override
    public Expense createExpense(Expense expense) throws SQLException {
        String query = "INSERT INTO expenses (description, amount, paidById, groupId, receiptImage, paymentMethod, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, expense.getDescription());
            stmt.setDouble(2, expense.getAmount());
            stmt.setInt(3, expense.getPaidBy().getUserId());
            stmt.setInt(4, expense.getGroup().getGroupId());
            stmt.setString(5, expense.getReceiptImage());
            stmt.setString(6, expense.getPaymentMethod());
            stmt.setString(7, expense.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating expense failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expense.setExpenseId(generatedKeys.getInt(1));

                    // Add participants and their shares
                    for (Map.Entry<User, Double> entry : expense.getShares().entrySet()) {
                        addParticipantToExpense(expense.getExpenseId(), entry.getKey().getUserId(), entry.getValue());
                    }
                } else {
                    throw new SQLException("Creating expense failed, no ID obtained.");
                }
            }
        }

        return expense;
    }

    @Override
    public boolean updateExpense(Expense expense) throws SQLException {
        String query = "UPDATE expenses SET description = ?, amount = ?, receiptImage = ?, paymentMethod = ?, status = ? "
                +
                "WHERE expenseId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, expense.getDescription());
            stmt.setDouble(2, expense.getAmount());
            stmt.setString(3, expense.getReceiptImage());
            stmt.setString(4, expense.getPaymentMethod());
            stmt.setString(5, expense.getStatus());
            stmt.setInt(6, expense.getExpenseId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteExpense(int expenseId) throws SQLException {
        // First delete all participants
        String deleteParticipants = "DELETE FROM expense_participants WHERE expenseId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteParticipants)) {
            stmt.setInt(1, expenseId);
            stmt.executeUpdate();
        }

        // Then delete the expense itself
        String deleteExpense = "DELETE FROM expenses WHERE expenseId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteExpense)) {
            stmt.setInt(1, expenseId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public Expense getExpenseById(int expenseId) throws SQLException {
        String query = "SELECT * FROM expenses WHERE expenseId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, expenseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractExpenseFromResultSet(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Expense> getExpensesByGroup(int groupId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses WHERE groupId = ? ORDER BY date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(extractExpenseFromResultSet(rs));
                }
            }
        }

        return expenses;
    }

    @Override
    public List<Expense> getExpensesPaidByUser(int userId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses WHERE paidById = ? ORDER BY date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(extractExpenseFromResultSet(rs));
                }
            }
        }

        return expenses;
    }

    @Override
    public List<Expense> getExpensesWhereUserParticipated(int userId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT e.* FROM expenses e " +
                "JOIN expense_participants ep ON e.expenseId = ep.expenseId " +
                "WHERE ep.userId = ? ORDER BY e.date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(extractExpenseFromResultSet(rs));
                }
            }
        }

        return expenses;
    }

    @Override
    public Map<User, Double> getExpenseParticipants(int expenseId) throws SQLException {
        Map<User, Double> shares = new HashMap<>();
        List<User> participants = new ArrayList<>();
        String query = "SELECT ep.*, u.* FROM expense_participants ep " +
                "JOIN users u ON ep.userId = u.userId " +
                "WHERE ep.expenseId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, expenseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("userId"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("fullName"));

                    double share = rs.getDouble("share");

                    shares.put(user, share);
                    participants.add(user);
                }
            }
        }

        return shares;
    }

    @Override
    public boolean addParticipantToExpense(int expenseId, int userId, double share) throws SQLException {
        String query = "INSERT INTO expense_participants (expenseId, userId, share, isPaid) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, expenseId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, share);
            stmt.setBoolean(4, false);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateParticipantShare(int expenseId, int userId, double share) throws SQLException {
        String query = "UPDATE expense_participants SET share = ? WHERE expenseId = ? AND userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, share);
            stmt.setInt(2, expenseId);
            stmt.setInt(3, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateParticipantPaidStatus(int expenseId, int userId, boolean isPaid) throws SQLException {
        String query = "UPDATE expense_participants SET isPaid = ? WHERE expenseId = ? AND userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, isPaid);
            stmt.setInt(2, expenseId);
            stmt.setInt(3, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public double calculateUserOwes(int groupId, int userId) throws SQLException {
        double totalOwed = 0.0;

        // Calculate what the user owes to others
        String query = "SELECT SUM(ep.share) as totalOwed FROM expense_participants ep " +
                "JOIN expenses e ON ep.expenseId = e.expenseId " +
                "WHERE e.groupId = ? AND ep.userId = ? AND e.paidById != ? AND ep.isPaid = FALSE";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getObject("totalOwed") != null) {
                    totalOwed = rs.getDouble("totalOwed");
                }
            }
        }

        return totalOwed;
    }

    @Override
    public double calculateUserIsOwed(int groupId, int userId) throws SQLException {
        double totalOwed = 0.0;

        // Calculate what others owe to the user
        String query = "SELECT SUM(ep.share) as totalOwed FROM expense_participants ep " +
                "JOIN expenses e ON ep.expenseId = e.expenseId " +
                "WHERE e.groupId = ? AND e.paidById = ? AND ep.userId != ? AND ep.isPaid = FALSE";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getObject("totalOwed") != null) {
                    totalOwed = rs.getDouble("totalOwed");
                }
            }
        }

        return totalOwed;
    }

    private Expense extractExpenseFromResultSet(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setExpenseId(rs.getInt("expenseId"));
        expense.setDescription(rs.getString("description"));
        expense.setAmount(rs.getDouble("amount"));

        // Get the user who paid
        int paidById = rs.getInt("paidById");
        User paidBy = userDAO.get(paidById);
        expense.setPaidBy(paidBy);

        // Get the group
        int groupId = rs.getInt("groupId");
        Group group = groupDAO.get(groupId);
        expense.setGroup(group);

        // Convert java.sql.Timestamp to LocalDateTime
        Timestamp timestamp = rs.getTimestamp("date");
        if (timestamp != null) {
            expense.setDate(timestamp.toLocalDateTime());
        }

        expense.setReceiptImage(rs.getString("receiptImage"));
        expense.setPaymentMethod(rs.getString("paymentMethod"));
        expense.setStatus(rs.getString("status"));

        // Load participants and their shares
        Map<User, Double> shares = getExpenseParticipants(expense.getExpenseId());
        expense.setShares(shares);

        // Set participants list from the shares
        List<User> participants = new ArrayList<>(shares.keySet());
        expense.setParticipants(participants);

        return expense;
    }
}