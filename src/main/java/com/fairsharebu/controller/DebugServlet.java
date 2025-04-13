package com.fairsharebu.controller;

import com.fairsharebu.dao.*;
import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Debug servlet to diagnose database and application issues
 */
@WebServlet(name = "DebugServlet", urlPatterns = { "/debug" })
public class DebugServlet extends HttpServlet {

    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            groupDAO = new GroupDAOImpl();
            userDAO = new UserDAOImpl();
            expenseDAO = new ExpenseDAOImpl();
        } catch (SQLException e) {
            throw new ServletException("Error initializing DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Debug Information</title></head><body>");
        out.println("<h1>Debug Information</h1>");

        try {
            // Test database connection
            out.println("<h2>Database Connection Test</h2>");
            out.println("<p>Connection status: OK</p>");

            // Test user retrieval
            out.println("<h2>User Data Test</h2>");
            User testUser = userDAO.get(1);
            if (testUser != null) {
                out.println("<p>Successfully retrieved user: " + testUser.getUsername() + "</p>");
            } else {
                out.println("<p>No user found with ID 1</p>");
            }

            // Test group retrieval
            out.println("<h2>Group Data Test</h2>");
            try {
                Group testGroup = groupDAO.getGroupById(4); // Group ID from URL
                if (testGroup != null) {
                    out.println("<p>Successfully retrieved group: " + testGroup.getName() + "</p>");
                    out.println("<p>Group creator: " + testGroup.getCreator().getFullName() + "</p>");

                    // Test group membership
                    out.println("<h3>Group Membership Test</h3>");
                    if (testUser != null) {
                        boolean isMember = groupDAO.isUserMemberOfGroup(testUser.getUserId(), testGroup.getGroupId());
                        out.println("<p>Is user a member of group: " + isMember + "</p>");
                    }

                    // Test group members retrieval
                    out.println("<h3>Group Members Test</h3>");
                    List<User> members = groupDAO.getGroupMembers(testGroup.getGroupId());
                    out.println("<p>Number of members: " + members.size() + "</p>");
                    out.println("<ul>");
                    for (User member : members) {
                        out.println("<li>" + member.getFullName() + " (ID: " + member.getUserId() + ")</li>");
                    }
                    out.println("</ul>");
                } else {
                    out.println("<p>No group found with ID 4</p>");
                }
            } catch (Exception e) {
                out.println("<p style='color:red'>Error retrieving group: " + e.getMessage() + "</p>");
                out.println("<pre>");
                e.printStackTrace(out);
                out.println("</pre>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body></html>");
    }
}