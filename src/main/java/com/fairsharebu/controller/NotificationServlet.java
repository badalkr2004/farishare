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
@WebServlet(name = "NotificationServlet", urlPatterns = { "/notifications/*" })
public class NotificationServlet extends HttpServlet {

    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            notificationDAO = new NotificationDAOImpl();
        } catch (SQLException e) {
            throw new ServletException("Error initializing DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get session and check if user is logged in
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                // List all notifications for the user
                List<Notification> userNotifications = notificationDAO.getNotificationsByUser(user.getUserId());
                request.setAttribute("notifications", userNotifications);
                request.getRequestDispatcher("/WEB-INF/jsp/notifications/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/unread")) {
                // List unread notifications for the user
                List<Notification> unreadNotifications = notificationDAO.getUnreadNotificationsByUser(user.getUserId());
                request.setAttribute("notifications", unreadNotifications);
                request.getRequestDispatcher("/WEB-INF/jsp/notifications/list.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/view/")) {
                // View a specific notification
                int notificationId = Integer.parseInt(pathInfo.substring(6));
                Notification notification = notificationDAO.getNotificationById(notificationId);

                if (notification == null) {
                    request.setAttribute("errorMessage", "Notification not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the notification belongs to the user
                if (notification.getRecipient().getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "You do not have access to this notification.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Mark the notification as read
                if (!notification.isRead()) {
                    notificationDAO.markNotificationAsRead(notificationId);
                    notification.setRead(true);
                }

                request.setAttribute("notification", notification);

                // If the notification has a link, redirect to it
                if (notification.getLink() != null && !notification.getLink().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + notification.getLink());
                    return;
                }

                request.getRequestDispatcher("/WEB-INF/jsp/notifications/view.jsp").forward(request, response);
            } else {
                // Invalid URL
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get session and check if user is logged in
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo.equals("/markRead")) {
                // Mark a specific notification as read
                int notificationId = Integer.parseInt(request.getParameter("notificationId"));
                Notification notification = notificationDAO.getNotificationById(notificationId);

                if (notification == null) {
                    request.setAttribute("errorMessage", "Notification not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the notification belongs to the user
                if (notification.getRecipient().getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "You do not have access to this notification.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Mark the notification as read
                notificationDAO.markNotificationAsRead(notificationId);

                // Redirect back to the notifications list
                response.sendRedirect(request.getContextPath() + "/notifications/list");
            } else if (pathInfo.equals("/markAllRead")) {
                // Mark all notifications as read
                notificationDAO.markAllNotificationsAsRead(user.getUserId());

                // Redirect back to the notifications list
                response.sendRedirect(request.getContextPath() + "/notifications/list");
            } else {
                // Invalid URL
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}