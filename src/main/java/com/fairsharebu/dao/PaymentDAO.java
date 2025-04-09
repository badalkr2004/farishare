package com.fairsharebu.dao;

import com.fairsharebu.model.Payment;
import java.sql.SQLException;
import java.util.List;

public interface PaymentDAO {
    Payment getPaymentById(int paymentId) throws SQLException;

    List<Payment> getPaymentsByUser(int userId) throws SQLException;

    List<Payment> getPaymentsByGroup(int groupId) throws SQLException;

    void addPayment(Payment payment) throws SQLException;

    void updatePayment(Payment payment) throws SQLException;

    void deletePayment(int paymentId) throws SQLException;
}