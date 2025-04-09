package com.fairsharebu.controller;

import com.fairsharebu.dao.NotificationDAO;
import com.fairsharebu.dao.NotificationDAOImpl;
import com.fairsharebu.model.Notification;
import com.fairsharebu.model.User;
import com.fairsharebu.util.JWTUtil;

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
 * Servlet to handle notification-related requests
 */
@WebServlet("/notifications/*")
public class NotificationServlet extends HttpServlet {

    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        try {
            notificationDAO = new NotificationDAOImpl();
        } catch (SQLException e) {
            throw new ServletException("Error initializing NotificationDAO", e);
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
                // List all notifications
                List<Notification> notifications = notificationDAO.getNotificationsForUser(user.getUserId());
                request.setAttribute("notifications", notifications);
                request.getRequestDispatcher("/WEB-INF/jsp/notifications/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/mark-read")) {
                // Mark notification as read
                int notificationId = Integer.parseInt(request.getParameter("id"));
                notificationDAO.markAsRead(notificationId);
                response.sendRedirect(request.getContextPath() + "/notifications");
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

            if ("markAllRead".equals(action)) {
                notificationDAO.markAllAsRead(user.getUserId());
            } else if ("delete".equals(action)) {
                int notificationId = Integer.parseInt(request.getParameter("id"));
                notificationDAO.deleteNotification(notificationId);
            }

            response.sendRedirect(request.getContextPath() + "/notifications");
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}