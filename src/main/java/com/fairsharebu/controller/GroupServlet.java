package com.fairsharebu.controller;

import com.fairsharebu.dao.GroupDAO;
import com.fairsharebu.dao.GroupDAOImpl;
import com.fairsharebu.dao.UserDAO;
import com.fairsharebu.dao.UserDAOImpl;
import com.fairsharebu.model.Group;
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
 * Servlet to handle group-related requests
 * This servlet is deprecated, please use GroupController instead
 * 
 * @deprecated Use GroupController instead
 */
@WebServlet(name = "GroupServlet", urlPatterns = { "/legacy-groups/*" })
public class GroupServlet extends HttpServlet {

    private GroupDAO groupDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            groupDAO = new GroupDAOImpl();
            userDAO = new UserDAOImpl();
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
                // List all groups the user is a member of
                List<Group> userGroups = groupDAO.getGroupsByMember(user.getUserId());
                request.setAttribute("groups", userGroups);
                request.getRequestDispatcher("/WEB-INF/jsp/groups/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/create")) {
                // Show the create group form
                request.getRequestDispatcher("/WEB-INF/jsp/groups/create.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/view/")) {
                // View a specific group
                int groupId = Integer.parseInt(pathInfo.substring(6));
                Group group = groupDAO.getGroupById(groupId);

                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is a member of the group
                List<User> members = groupDAO.getGroupMembers(groupId);
                boolean isMember = members.stream().anyMatch(member -> member.getUserId() == user.getUserId());

                if (!isMember) {
                    request.setAttribute("errorMessage", "You are not a member of this group.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("group", group);
                request.setAttribute("members", members);
                request.getRequestDispatcher("/WEB-INF/jsp/groups/view.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/edit/")) {
                // Edit a specific group
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

                request.setAttribute("group", group);
                request.getRequestDispatcher("/WEB-INF/jsp/groups/edit.jsp").forward(request, response);
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
            if (pathInfo.equals("/create")) {
                // Create a new group
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String location = request.getParameter("location");

                if (name == null || name.trim().isEmpty()) {
                    request.setAttribute("error", "Group name is required");
                    request.getRequestDispatcher("/WEB-INF/jsp/groups/create.jsp").forward(request, response);
                    return;
                }

                Group newGroup = new Group();
                newGroup.setName(name);
                newGroup.setDescription(description);
                newGroup.setCreator(user);
                newGroup.setLocation(location);

                Group createdGroup = groupDAO.createGroup(newGroup);

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

                if (name == null || name.trim().isEmpty()) {
                    request.setAttribute("error", "Group name is required");
                    request.setAttribute("group", group);
                    request.getRequestDispatcher("/WEB-INF/jsp/groups/edit.jsp").forward(request, response);
                    return;
                }

                group.setName(name);
                group.setDescription(description);
                group.setLocation(location);

                groupDAO.updateGroup(group);

                response.sendRedirect(request.getContextPath() + "/groups/view/" + group.getGroupId());
            } else if (pathInfo.equals("/addMember")) {
                // Add a member to a group
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                String username = request.getParameter("username");

                Group group = groupDAO.getGroupById(groupId);

                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is a member of the group
                List<User> members = groupDAO.getGroupMembers(groupId);
                boolean isMember = members.stream().anyMatch(member -> member.getUserId() == user.getUserId());

                if (!isMember) {
                    request.setAttribute("errorMessage", "You are not a member of this group.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Find the user to add
                User userToAdd = userDAO.findByUsername(username);

                if (userToAdd == null) {
                    request.setAttribute("group", group);
                    request.setAttribute("members", members);
                    request.setAttribute("error", "User not found: " + username);
                    request.getRequestDispatcher("/WEB-INF/jsp/groups/view.jsp").forward(request, response);
                    return;
                }

                // Check if the user is already a member
                boolean isAlreadyMember = members.stream()
                        .anyMatch(member -> member.getUserId() == userToAdd.getUserId());

                if (isAlreadyMember) {
                    request.setAttribute("group", group);
                    request.setAttribute("members", members);
                    request.setAttribute("error", "User is already a member of this group.");
                    request.getRequestDispatcher("/WEB-INF/jsp/groups/view.jsp").forward(request, response);
                    return;
                }

                // Add the user to the group
                groupDAO.addUserToGroup(groupId, userToAdd.getUserId());

                response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
            } else if (pathInfo.equals("/removeMember")) {
                // Remove a member from a group
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                int userId = Integer.parseInt(request.getParameter("userId"));

                Group group = groupDAO.getGroupById(groupId);

                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is the creator of the group
                if (group.getCreator().getUserId() != user.getUserId() && userId != user.getUserId()) {
                    request.setAttribute("errorMessage", "You are not authorized to remove members from this group.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Remove the user from the group
                groupDAO.removeUserFromGroup(groupId, userId);

                response.sendRedirect(request.getContextPath() + "/groups/view/" + groupId);
            } else if (pathInfo.equals("/delete")) {
                // Delete a group
                int groupId = Integer.parseInt(request.getParameter("groupId"));

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

                // Delete the group
                groupDAO.deleteGroup(groupId);

                response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
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