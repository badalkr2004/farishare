package com.fairsharebu.controller;

import com.fairsharebu.dao.*;
import com.fairsharebu.model.Payment;
import com.fairsharebu.model.User;
import com.fairsharebu.model.Group;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebServlet("/payments/*")
public class PaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO;
    private GroupDAO groupDAO;
    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        try {
            paymentDAO = new PaymentDAOImpl();
            groupDAO = new GroupDAOImpl();
            notificationDAO = new NotificationDAOImpl();
        } catch (SQLException e) {
            throw new ServletException("Error initializing DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // List all payments
                List<Payment> payments = paymentDAO.getPaymentsByUser(user.getUserId());
                request.setAttribute("payments", payments);
                request.getRequestDispatcher("/WEB-INF/jsp/payments/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/create")) {
                // Show payment form
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                Group group = groupDAO.getGroupById(groupId);
                List<User> members = groupDAO.getGroupMembers(groupId);

                request.setAttribute("group", group);
                request.setAttribute("members", members);
                request.getRequestDispatcher("/WEB-INF/jsp/payments/create.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String action = request.getParameter("action");

            if ("create".equals(action)) {
                // Create new payment
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                int receiverId = Integer.parseInt(request.getParameter("receiverId"));
                double amount = Double.parseDouble(request.getParameter("amount"));
                String description = request.getParameter("description");

                Payment payment = new Payment();
                payment.setPayer(user);

                User receiver = new User();
                receiver.setUserId(receiverId);
                payment.setReceiver(receiver);

                payment.setAmount(amount);
                payment.setDescription(description);

                Group group = new Group();
                group.setGroupId(groupId);
                payment.setGroup(group);

                payment.setPaymentDate(new Date());
                payment.setStatus("COMPLETED");

                paymentDAO.addPayment(payment);

                // Create notification for receiver
                notificationDAO.createNotification(receiverId,
                        "Payment Received",
                        user.getFullName() + " sent you $" + amount + " for " + description,
                        "/payments/view?id=" + payment.getPaymentId());

                response.sendRedirect(request.getContextPath() + "/payments");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}