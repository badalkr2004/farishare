package com.fairsharebu.controller;

import com.fairsharebu.dao.UserDAO;
import com.fairsharebu.dao.UserDAOImpl;
import com.fairsharebu.model.User;
import com.fairsharebu.util.JWTUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet to handle user authentication (login, registration, logout).
 */
@WebServlet(name = "AuthServlet", urlPatterns = { "/auth/*" })
public class AuthServlet extends HttpServlet {
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
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Default - redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else if (pathInfo.equals("/logout")) {
            // Handle logout
            logout(request, response);
        } else {
            // Unknown path - redirect to login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Default - redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else if (pathInfo.equals("/login")) {
            // Handle login
            login(request, response);
        } else if (pathInfo.equals("/register")) {
            // Handle registration
            register(request, response);
        } else {
            // Unknown path - redirect to login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    /**
     * Handle user login.
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        try {
            User user = userDAO.authenticate(username, password);

            if (user != null) {
                // Authentication successful
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());

                // If "Remember Me" was checked, create a JWT token and store in a cookie
                if (rememberMe != null && rememberMe.equals("on")) {
                    String token = JWTUtil.generateToken(user.getUserId(), user.getUsername());

                    Cookie authCookie = new Cookie("authToken", token);
                    authCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                    authCookie.setPath("/");
                    authCookie.setHttpOnly(true);

                    response.addCookie(authCookie);
                }

                // Redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                // Authentication failed
                request.setAttribute("errorMessage", "Invalid username or password");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * Handle user registration.
     */
    private void register(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");

        // Validate input
        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            // Check if username already exists
            User existingUser = userDAO.findByUsername(username);
            if (existingUser != null) {
                request.setAttribute("errorMessage", "Username already exists");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Check if email already exists
            existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                request.setAttribute("errorMessage", "Email already exists");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Create user
            User user = new User(username, email, password, fullName, phoneNumber);
            user = userDAO.insert(user);

            // Create session for the new user
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());

            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    /**
     * Handle user logout.
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Remove auth cookie if exists
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("authToken")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}