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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/payments/*")
public class PaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO;
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private NotificationDAO notificationDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        try {
            paymentDAO = new PaymentDAOImpl();
            groupDAO = new GroupDAOImpl();
            userDAO = new UserDAOImpl();
            notificationDAO = new NotificationDAOImpl();
            expenseDAO = new ExpenseDAOImpl();
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
                List<Payment> sentPayments = new ArrayList<>();
                List<Payment> receivedPayments = new ArrayList<>();

                for (Payment payment : payments) {
                    // Load full user objects
                    payment.setPayer(userDAO.get(payment.getPayer().getUserId()));
                    payment.setReceiver(userDAO.get(payment.getReceiver().getUserId()));
                    payment.setGroup(groupDAO.getGroupById(payment.getGroup().getGroupId()));

                    if (payment.getPayer().getUserId() == user.getUserId()) {
                        sentPayments.add(payment);
                    } else {
                        receivedPayments.add(payment);
                    }
                }

                request.setAttribute("payments", payments);
                request.setAttribute("sentPayments", sentPayments);
                request.setAttribute("receivedPayments", receivedPayments);
                request.getRequestDispatcher("/WEB-INF/jsp/payments/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/create")) {
                // Show payment form
                List<Group> groups = groupDAO.getGroupsByMember(user.getUserId());
                request.setAttribute("groups", groups);

                // If group ID is specified, load the group members
                String groupIdParam = request.getParameter("groupId");
                if (groupIdParam != null && !groupIdParam.isEmpty()) {
                    int groupId = Integer.parseInt(groupIdParam);
                    Group group = groupDAO.getGroupById(groupId);
                    List<User> members = groupDAO.getGroupMembers(groupId);

                    request.setAttribute("group", group);
                    request.setAttribute("members", members);

                    // Get the balances within this group to show to the user
                    for (User member : members) {
                        if (member.getUserId() != user.getUserId()) {
                            double amountOwed = expenseDAO.getAmountOwedByUser(groupId, user.getUserId());
                            double amountOwedTo = expenseDAO.getAmountOwedToUser(groupId, user.getUserId());

                            request.setAttribute("amountOwed", amountOwed);
                            request.setAttribute("amountOwedTo", amountOwedTo);
                            break; // Just get the first one for now
                        }
                    }
                }

                request.getRequestDispatcher("/WEB-INF/jsp/payments/create.jsp").forward(request, response);
            } else if (pathInfo.equals("/view")) {
                // View payment details
                int paymentId = Integer.parseInt(request.getParameter("id"));
                Payment payment = paymentDAO.getPaymentById(paymentId);

                if (payment == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // Load full user objects
                payment.setPayer(userDAO.get(payment.getPayer().getUserId()));
                payment.setReceiver(userDAO.get(payment.getReceiver().getUserId()));
                payment.setGroup(groupDAO.getGroupById(payment.getGroup().getGroupId()));

                request.setAttribute("payment", payment);
                request.getRequestDispatcher("/WEB-INF/jsp/payments/view.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter format");
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

                // Validate the payment
                if (amount <= 0) {
                    request.setAttribute("errorMessage", "Payment amount must be greater than zero");
                    request.getRequestDispatcher("/WEB-INF/jsp/payments/create.jsp").forward(request, response);
                    return;
                }

                // Check if user is member of the group
                if (!groupDAO.isUserMemberOfGroup(user.getUserId(), groupId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a member of this group");
                    return;
                }

                // Get the receiver user
                User receiver = userDAO.get(receiverId);
                if (receiver == null) {
                    request.setAttribute("errorMessage", "Invalid recipient");
                    request.getRequestDispatcher("/WEB-INF/jsp/payments/create.jsp").forward(request, response);
                    return;
                }

                // Create the payment object
                Payment payment = new Payment();
                payment.setPayer(user);
                payment.setReceiver(receiver);
                payment.setAmount(amount);
                payment.setDescription(description);

                Group group = groupDAO.getGroupById(groupId);
                payment.setGroup(group);

                payment.setPaymentDate(new Date());
                payment.setStatus("COMPLETED");

                // Add the payment to the database
                paymentDAO.addPayment(payment);

                // Create notification for receiver
                notificationDAO.createNotification(receiverId,
                        "Payment Received",
                        user.getFullName() + " sent you ₹" + amount + " for " + description,
                        "/payments/view?id=" + payment.getPaymentId());

                // Redirect to payments list
                response.sendRedirect(request.getContextPath() + "/payments");
            } else if ("cancel".equals(action)) {
                // Cancel a payment (optional feature - only if payment is in PENDING status)
                int paymentId = Integer.parseInt(request.getParameter("id"));
                Payment payment = paymentDAO.getPaymentById(paymentId);

                if (payment == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // Ensure only the payer can cancel a payment
                if (payment.getPayer().getUserId() != user.getUserId()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                // Ensure payment is in PENDING status
                if (!"PENDING".equals(payment.getStatus())) {
                    request.setAttribute("errorMessage", "Only pending payments can be cancelled");
                    request.getRequestDispatcher("/WEB-INF/jsp/payments/view.jsp").forward(request, response);
                    return;
                }

                // Update payment status to CANCELLED
                payment.setStatus("CANCELLED");
                paymentDAO.updatePayment(payment);

                // Create notification for receiver
                notificationDAO.createNotification(payment.getReceiver().getUserId(),
                        "Payment Cancelled",
                        user.getFullName() + " cancelled a payment of ₹" + payment.getAmount(),
                        "/payments/view?id=" + payment.getPaymentId());

                // Redirect to payments list
                response.sendRedirect(request.getContextPath() + "/payments");
            }
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/payments/create.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid number format in parameters");
            request.getRequestDispatcher("/WEB-INF/jsp/payments/create.jsp").forward(request, response);
        }
    }
}