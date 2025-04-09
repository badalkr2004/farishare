package com.fairsharebu.dao;

import com.fairsharebu.model.Payment;
import com.fairsharebu.model.User;
import com.fairsharebu.model.Group;
import com.fairsharebu.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private Connection conn;

    public PaymentDAOImpl() throws SQLException {
        this.conn = DatabaseUtil.getConnection();
    }

    @Override
    public Payment getPaymentById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Payment> getPaymentsByUser(int userId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE payer_id = ? OR receiver_id = ? ORDER BY payment_date DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentsByGroup(int groupId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE group_id = ? ORDER BY payment_date DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        }
        return payments;
    }

    @Override
    public void addPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (payer_id, receiver_id, amount, description, group_id, payment_date, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getPayer().getUserId());
            stmt.setInt(2, payment.getReceiver().getUserId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getDescription());
            stmt.setInt(5, payment.getGroup().getGroupId());
            stmt.setTimestamp(6, new Timestamp(payment.getPaymentDate().getTime()));
            stmt.setString(7, payment.getStatus());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                payment.setPaymentId(rs.getInt(1));
            }
        }
    }

    @Override
    public void updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE payment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getStatus());
            stmt.setInt(2, payment.getPaymentId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.executeUpdate();
        }
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));

        User payer = new User();
        payer.setUserId(rs.getInt("payer_id"));
        payment.setPayer(payer);

        User receiver = new User();
        receiver.setUserId(rs.getInt("receiver_id"));
        payment.setReceiver(receiver);

        payment.setAmount(rs.getDouble("amount"));
        payment.setDescription(rs.getString("description"));

        Group group = new Group();
        group.setGroupId(rs.getInt("group_id"));
        payment.setGroup(group);

        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setStatus(rs.getString("status"));

        return payment;
    }
}