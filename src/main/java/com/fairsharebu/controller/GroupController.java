package com.fairsharebu.controller;

import com.fairsharebu.dao.*;
import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;
import com.fairsharebu.model.Expense;
import com.fairsharebu.model.Balance;
import com.fairsharebu.util.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Controller to handle group-related requests and forward to appropriate JSP
 * views
 */
@WebServlet(name = "GroupController", urlPatterns = "/groups/*")
@MultipartConfig
public class GroupController extends HttpServlet {

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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        try {
            if (pathInfo.equals("/") || pathInfo.equals("/list")) {
                handleListGroups(request, response, user);
            } else if (pathInfo.equals("/create")) {
                handleCreateForm(request, response);
            } else if (pathInfo.startsWith("/view/")) {
                handleViewGroup(request, response, user, pathInfo);
            } else if (pathInfo.startsWith("/edit/")) {
                handleEditForm(request, response, user, pathInfo);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private void handleListGroups(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {
        List<Group> userGroups = groupDAO.getGroupsByMember(user.getUserId());
        request.setAttribute("groups", userGroups);
        request.getRequestDispatcher("/WEB-INF/jsp/groups/list.jsp").forward(request, response);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/groups/create.jsp").forward(request, response);
    }

    private void handleViewGroup(HttpServletRequest request, HttpServletResponse response, User user, String pathInfo)
            throws ServletException, IOException, SQLException {
        try {
            int groupId = Integer.parseInt(pathInfo.substring(6));
            Group group = groupDAO.getGroupById(groupId);

            if (group == null) {
                request.setAttribute("errorMessage", "Group not found.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // Check if the user is a member of the group
            boolean isMember = groupDAO.isUserMemberOfGroup(user.getUserId(), groupId);

            if (!isMember) {
                request.setAttribute("errorMessage", "You are not a member of this group.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // Get group members
            List<User> members = groupDAO.getGroupMembers(groupId);

            // Get recent expenses
            List<Expense> recentExpenses = expenseDAO.getRecentExpensesByGroup(groupId, 5);

            // Calculate balances for each member
            List<Balance> balances = new ArrayList<>();
            for (User member : members) {
                double owes = expenseDAO.getAmountOwedByUser(groupId, member.getUserId());
                double isOwed = expenseDAO.getAmountOwedToUser(groupId, member.getUserId());
                double netBalance = isOwed - owes;
                balances.add(new Balance(member, netBalance));
            }

            // Set attributes for the view
            request.setAttribute("group", group);
            request.setAttribute("members", members);
            request.setAttribute("recentExpenses", recentExpenses);
            request.setAttribute("balances", balances);
            request.setAttribute("currentUser", user);

            // Forward to the view page
            request.getRequestDispatcher("/WEB-INF/jsp/groups/view.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group ID");
        }
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, User user, String pathInfo)
            throws ServletException, IOException, SQLException {
        try {
            int groupId = Integer.parseInt(pathInfo.substring(6));
            Group group = groupDAO.getGroupById(groupId);

            if (group == null) {
                request.setAttribute("errorMessage", "Group not found.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            if (group.getCreator().getUserId() != user.getUserId()) {
                request.setAttribute("errorMessage", "You are not authorized to edit this group.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            request.setAttribute("group", group);
            request.getRequestDispatcher("/WEB-INF/jsp/groups/edit.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group ID");
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
        if (pathInfo == null) {
            pathInfo = "/";
        }

        try {
            if (pathInfo.equals("/create")) {
                // Create a new group
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String location = request.getParameter("location");
                boolean isPrivate = request.getParameter("privateGroup") != null;

                if (name == null || name.trim().isEmpty()) {
                    request.setAttribute("error", "Group name is required");
                    request.getRequestDispatcher("/WEB-INF/jsp/groups/create.jsp").forward(request, response);
                    return;
                }

                Group newGroup = new Group();
                newGroup.setName(name);
                newGroup.setDescription(description);
                newGroup.setLocation(location);
                newGroup.setCreator(user);
                newGroup.setPrivateGroup(isPrivate);

                Group createdGroup = groupDAO.createGroup(newGroup);

                // Process invited members if any
                String[] memberEmails = request.getParameterValues("members");
                if (memberEmails != null) {
                    for (String email : memberEmails) {
                        if (email != null && !email.trim().isEmpty()) {
                            User member = userDAO.findByEmail(email.trim());
                            if (member != null) {
                                groupDAO.addUserToGroup(createdGroup.getGroupId(), member.getUserId());
                            }
                        }
                    }
                }

                response.sendRedirect(request.getContextPath() + "/groups/view/" + createdGroup.getGroupId());
            } else if (pathInfo.startsWith("/edit/")) {
                // Update an existing group
                int groupId = Integer.parseInt(pathInfo.substring(6));
                Group group = groupDAO.getGroupById(groupId);

                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is the creator of the group
                if (group.getCreator().getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "You are not authorized to edit this group.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String location = request.getParameter("location");
                boolean isPrivate = request.getParameter("privateGroup") != null;
                boolean removeImage = request.getParameter("removeImage") != null;

                if (name == null || name.trim().isEmpty()) {
                    request.setAttribute("error", "Group name is required");
                    request.setAttribute("group", group);
                    request.getRequestDispatcher("/WEB-INF/jsp/groups/edit.jsp").forward(request, response);
                    return;
                }

                group.setName(name);
                group.setDescription(description);
                group.setLocation(location);
                group.setPrivateGroup(isPrivate);

                if (removeImage) {
                    group.setGroupImage(null);
                }

                groupDAO.updateGroup(group);

                request.setAttribute("success", "Group updated successfully");
                request.setAttribute("group", group);
                request.getRequestDispatcher("/WEB-INF/jsp/groups/edit.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/delete/")) {
                // Delete a group
                int groupId = Integer.parseInt(pathInfo.substring(8));
                Group group = groupDAO.getGroupById(groupId);

                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is the creator of the group
                if (group.getCreator().getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "You are not authorized to delete this group.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                groupDAO.deleteGroup(groupId);
                response.sendRedirect(request.getContextPath() + "/groups/");
            } else if (pathInfo.startsWith("/invite/")) {
                // Invite a user to a group
                int groupId = Integer.parseInt(pathInfo.substring(8));
                String email = request.getParameter("email");

                Group group = groupDAO.getGroupById(groupId);
                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if user has permission to invite
                if (group.getCreator().getUserId() != user.getUserId()) {
                    request.setAttribute("error", "Only the group creator can send invitations.");
                    response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
                    return;
                }

                if (email == null || email.trim().isEmpty()) {
                    request.setAttribute("error", "Email address is required");
                    response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
                    return;
                }

                User invitedUser = userDAO.findByEmail(email.trim());
                if (invitedUser == null) {
                    request.setAttribute("error", "User with email " + email + " not found");
                    response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
                    return;
                }

                // Check if already a member
                if (groupDAO.isUserMemberOfGroup(invitedUser.getUserId(), groupId)) {
                    request.setAttribute("error", "User is already a member of this group");
                    response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
                    return;
                }

                // Add the user to the group
                groupDAO.addUserToGroup(groupId, invitedUser.getUserId());

                request.setAttribute("success", "User " + invitedUser.getFullName() + " added to the group");
                response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
            } else if (pathInfo.startsWith("/removeMember/")) {
                // Remove a member from a group
                int groupId = Integer.parseInt(pathInfo.substring(14));
                int memberId = Integer.parseInt(request.getParameter("memberId"));

                Group group = groupDAO.getGroupById(groupId);
                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if user has permission to remove members
                if (group.getCreator().getUserId() != user.getUserId() && memberId != user.getUserId()) {
                    request.setAttribute("error", "You don't have permission to remove this member");
                    response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
                    return;
                }

                // Cannot remove the creator
                if (memberId == group.getCreator().getUserId()) {
                    request.setAttribute("error", "Cannot remove the group creator");
                    response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
                    return;
                }

                // Remove the user from the group
                groupDAO.removeUserFromGroup(groupId, memberId);

                request.setAttribute("success", "Member removed from the group");
                response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
            } else {
                // Invalid URL
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group ID");
        }
    }
}