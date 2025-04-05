package com.fairsharebu.controller;

import com.fairsharebu.dao.UserDAO;
import com.fairsharebu.dao.UserDAOImpl;
import com.fairsharebu.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet to handle the dashboard page.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = { "/dashboard" })
public class DashboardServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        try {
            userDAO = new UserDAOImpl();
        } catch (SQLException e) {
            throw new ServletException("Error initializing UserDAO", e);
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