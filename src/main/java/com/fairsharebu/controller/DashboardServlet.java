package com.fairsharebu.controller;

import com.fairsharebu.dao.*;
import com.fairsharebu.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet to handle the dashboard page.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = { "/dashboard" })
public class DashboardServlet extends HttpServlet {
    private UserDAO userDAO;
    private GroupDAO groupDAO;
    private ExpenseDAO expenseDAO;
    private PaymentDAO paymentDAO;
    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        try {
            userDAO = new UserDAOImpl();
            groupDAO = new GroupDAOImpl();
            expenseDAO = new ExpenseDAOImpl();
            paymentDAO = new PaymentDAOImpl();
            notificationDAO = new NotificationDAOImpl();
        } catch (SQLException e) {
            throw new ServletException("Error initializing DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get user information
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            User user = userDAO.get(userId);

            if (user == null) {
                // Invalid user ID in session
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            // Set user in request for dashboard.jsp
            request.setAttribute("user", user);

            // Get user's groups
            List<Group> userGroups = groupDAO.getGroupsByMember(userId);
            request.setAttribute("userGroups", userGroups);

            // Get recent expenses
            List<Expense> recentExpenses = expenseDAO.getExpensesByUser(userId);
            request.setAttribute("recentExpenses", recentExpenses);

            // Get recent payments
            List<Payment> recentPayments = paymentDAO.getPaymentsByUser(userId);
            request.setAttribute("recentPayments", recentPayments);

            // Get recent notifications
            List<Notification> recentNotifications = notificationDAO.getNotificationsForUser(userId);
            request.setAttribute("recentNotifications", recentNotifications);

            // Count unread notifications
            int unreadCount = 0;
            for (Notification notification : recentNotifications) {
                if (!notification.isRead()) {
                    unreadCount++;
                }
            }
            request.setAttribute("unreadNotifications", unreadCount);

            // Calculate balances if there are groups
            if (!userGroups.isEmpty()) {
                double totalOwed = 0;
                double totalOwing = 0;

                for (Group group : userGroups) {
                    totalOwed += expenseDAO.getAmountOwedByUser(group.getGroupId(), userId);
                    totalOwing += expenseDAO.getAmountOwedToUser(group.getGroupId(), userId);
                }

                double netBalance = totalOwing - totalOwed;

                request.setAttribute("totalOwed", totalOwed);
                request.setAttribute("totalOwing", totalOwing);
                request.setAttribute("netBalance", netBalance);
            }

            // Forward to dashboard
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            // Log the error
            e.printStackTrace();

            // Show error page
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}