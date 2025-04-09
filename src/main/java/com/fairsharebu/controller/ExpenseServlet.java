package com.fairsharebu.controller;

import com.fairsharebu.dao.*;
import com.fairsharebu.model.Expense;
import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;
import com.fairsharebu.model.Notification;
import com.fairsharebu.util.JWTUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servlet to handle expense-related requests
 */
@WebServlet(name = "ExpenseServlet", urlPatterns = { "/expenses/*" })
@MultipartConfig
public class ExpenseServlet extends HttpServlet {

    private ExpenseDAO expenseDAO;
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            expenseDAO = new ExpenseDAOImpl();
            groupDAO = new GroupDAOImpl();
            userDAO = new UserDAOImpl();
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

        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");

        try {
            if ("list".equals(action) || pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                int userId = user.getUserId();
                // Get all expenses that involve the user
                List<Expense> expenses = expenseDAO.getExpensesByUser(userId);
                request.setAttribute("expenses", expenses);

                // Filter expenses paid by the user
                List<Expense> youPaidExpenses = expenses.stream()
                        .filter(expense -> expense.getPaidBy().getUserId() == userId)
                        .toList();
                request.setAttribute("youPaidExpenses", youPaidExpenses);

                // Filter expenses where the user owes money
                List<Expense> youOweExpenses = expenses.stream()
                        .filter(expense -> expense.getPaidBy().getUserId() != userId &&
                                expense.getParticipants().stream()
                                        .anyMatch(participant -> participant.getUserId() == userId))
                        .toList();
                request.setAttribute("youOweExpenses", youOweExpenses);

                // Get all groups the user is a member of
                List<Group> userGroups = groupDAO.getGroupsByMember(userId);

                // Calculate totals if there are groups
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

                // Get unread notifications count
                List<Notification> notifications = notificationDAO.getNotificationsForUser(userId);
                int unreadCount = 0;
                for (Notification notification : notifications) {
                    if (!notification.isRead()) {
                        unreadCount++;
                    }
                }
                request.setAttribute("unreadNotifications", unreadCount);

                request.getRequestDispatcher("/WEB-INF/jsp/expenses/list.jsp").forward(request, response);
            } else if ("create".equals(action)) {
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                Group group = groupDAO.getGroupById(groupId);
                request.setAttribute("group", group);
                request.getRequestDispatcher("/WEB-INF/jsp/expenses/create.jsp").forward(request, response);
            } else if ("view".equals(action)) {
                int expenseId = Integer.parseInt(request.getParameter("id"));
                Expense expense = expenseDAO.getExpenseById(expenseId);
                if (expense == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("expense", expense);
                request.getRequestDispatcher("/WEB-INF/jsp/expenses/view.jsp").forward(request, response);
            } else if (pathInfo.equals("/create")) {
                // Show the create expense form
                // Get the group ID if provided
                String groupIdParam = request.getParameter("groupId");
                if (groupIdParam != null && !groupIdParam.isEmpty()) {
                    int groupId = Integer.parseInt(groupIdParam);
                    Group group = groupDAO.getGroupById(groupId);

                    if (group != null) {
                        List<User> members = groupDAO.getGroupMembers(groupId);

                        // Check if the user is a member of the group
                        boolean isMember = members.stream().anyMatch(member -> member.getUserId() == user.getUserId());
                        if (!isMember) {
                            request.setAttribute("errorMessage", "You are not a member of this group.");
                            request.getRequestDispatcher("/error.jsp").forward(request, response);
                            return;
                        }

                        request.setAttribute("group", group);
                        request.setAttribute("members", members);
                    }
                }

                // Get all groups the user is a member of (for the group selector)
                List<Group> userGroups = groupDAO.getGroupsByMember(user.getUserId());
                request.setAttribute("userGroups", userGroups);

                request.getRequestDispatcher("/WEB-INF/jsp/expenses/create.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/view/")) {
                // View a specific expense
                int expenseId = Integer.parseInt(pathInfo.substring(6));
                Expense expense = expenseDAO.getExpenseById(expenseId);

                if (expense == null) {
                    request.setAttribute("errorMessage", "Expense not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is a participant or paid the expense
                boolean isParticipant = expense.getParticipants().stream()
                        .anyMatch(participant -> participant.getUserId() == user.getUserId());
                boolean isPayer = expense.getPaidBy().getUserId() == user.getUserId();

                if (!isParticipant && !isPayer) {
                    request.setAttribute("errorMessage", "You do not have access to this expense.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("expense", expense);
                request.getRequestDispatcher("/WEB-INF/jsp/expenses/view.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/edit/")) {
                // Edit a specific expense
                int expenseId = Integer.parseInt(pathInfo.substring(6));
                Expense expense = expenseDAO.getExpenseById(expenseId);

                if (expense == null) {
                    request.setAttribute("errorMessage", "Expense not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is the one who paid the expense
                if (expense.getPaidBy().getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "You are not authorized to edit this expense.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Get all members of the group
                List<User> members = groupDAO.getGroupMembers(expense.getGroup().getGroupId());

                request.setAttribute("expense", expense);
                request.setAttribute("members", members);
                request.getRequestDispatcher("/WEB-INF/jsp/expenses/edit.jsp").forward(request, response);
            } else if (pathInfo.equals("/group")) {
                // List all expenses for a specific group
                int groupId = Integer.parseInt(request.getParameter("id"));
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

                List<Expense> groupExpenses = expenseDAO.getExpensesByGroup(groupId);

                request.setAttribute("group", group);
                request.setAttribute("expenses", groupExpenses);
                request.setAttribute("members", members);

                // Calculate balances for each user in the group
                Map<Integer, Double> balances = new HashMap<>();
                for (User member : members) {
                    double owes = expenseDAO.getAmountOwedByUser(groupId, member.getUserId());
                    double isOwed = expenseDAO.getAmountOwedToUser(groupId, member.getUserId());
                    balances.put(member.getUserId(), isOwed - owes);
                }

                request.setAttribute("balances", balances);

                request.getRequestDispatcher("/WEB-INF/jsp/expenses/group.jsp").forward(request, response);
            } else if (pathInfo.equals("/settle")) {
                // Show the settlement form
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                int payerId = Integer.parseInt(request.getParameter("payerId"));
                int receiverId = Integer.parseInt(request.getParameter("receiverId"));

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

                // Get the users involved in the settlement
                User payer = userDAO.get(payerId);
                User receiver = userDAO.get(receiverId);

                if (payer == null || receiver == null) {
                    request.setAttribute("errorMessage", "Invalid users for settlement.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Calculate the settlement amount
                double payerOwes = expenseDAO.getAmountOwedByUser(groupId, payerId);

                request.setAttribute("group", group);
                request.setAttribute("payer", payer);
                request.setAttribute("receiver", receiver);
                request.setAttribute("amount", payerOwes);

                request.getRequestDispatcher("/WEB-INF/jsp/expenses/settle.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                handleCreateExpense(request, response, user);
            } else if ("update".equals(action)) {
                handleUpdateExpense(request, response, user);
            } else if ("delete".equals(action)) {
                handleDeleteExpense(request, response, user);
            } else if ("settle".equals(action)) {
                handleSettleExpense(request, response, user);
            } else {
                // Invalid URL
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private void handleCreateExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {
        String description = request.getParameter("description");
        double amount = Double.parseDouble(request.getParameter("amount"));
        int groupId = Integer.parseInt(request.getParameter("groupId"));
        String paymentMethod = request.getParameter("paymentMethod");
        String splitMethod = request.getParameter("splitMethod");

        if (description == null || description.trim().isEmpty()) {
            request.setAttribute("error", "Description is required");
            request.getRequestDispatcher("/WEB-INF/jsp/expenses/create.jsp").forward(request, response);
            return;
        }

        Group group = groupDAO.getGroupById(groupId);
        if (group == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Group not found");
            return;
        }

        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setPaidBy(user);
        expense.setGroup(group);
        expense.setPaymentMethod(paymentMethod);
        expense.setStatus("PENDING");

        // Handle receipt image upload
        Part filePart = request.getPart("receiptImage");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = UUID.randomUUID().toString() + getFileExtension(filePart);
            String uploadDir = getServletContext().getRealPath("/") + "uploads/receipts/";
            Files.createDirectories(Paths.get(uploadDir));
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(uploadDir, fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            expense.setReceiptImage(fileName);
        }

        // Handle expense splitting
        List<User> members = groupDAO.getGroupMembers(groupId);
        if ("equal".equals(splitMethod)) {
            expense.addParticipants(members);
            expense.splitEqually();
        } else if ("percentage".equals(splitMethod)) {
            Map<User, Double> percentages = new HashMap<>();
            for (User member : members) {
                String percentStr = request.getParameter("percent_" + member.getUserId());
                if (percentStr != null && !percentStr.isEmpty()) {
                    double percent = Double.parseDouble(percentStr);
                    if (percent > 0) {
                        percentages.put(member, percent);
                        expense.addParticipant(member);
                    }
                }
            }
            expense.splitByPercentage(percentages);
        } else if ("amount".equals(splitMethod)) {
            Map<User, Double> amounts = new HashMap<>();
            for (User member : members) {
                String amountStr = request.getParameter("amount_" + member.getUserId());
                if (amountStr != null && !amountStr.isEmpty()) {
                    double memberAmount = Double.parseDouble(amountStr);
                    if (memberAmount > 0) {
                        amounts.put(member, memberAmount);
                        expense.addParticipant(member);
                    }
                }
            }
            expense.splitByAmount(amounts);
        }

        expenseDAO.addExpense(expense);
        response.sendRedirect(request.getContextPath() + "/expenses?action=view&id=" + expense.getExpenseId());
    }

    private void handleUpdateExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {
        int expenseId = Integer.parseInt(request.getParameter("id"));
        Expense expense = expenseDAO.getExpenseById(expenseId);

        if (expense == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (expense.getPaidBy().getUserId() != user.getUserId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String description = request.getParameter("description");
        double amount = Double.parseDouble(request.getParameter("amount"));
        String paymentMethod = request.getParameter("paymentMethod");

        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setPaymentMethod(paymentMethod);

        // Handle receipt image
        Part filePart = request.getPart("receiptImage");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = UUID.randomUUID().toString() + getFileExtension(filePart);
            String uploadDir = getServletContext().getRealPath("/") + "uploads/receipts/";
            Files.createDirectories(Paths.get(uploadDir));

            // Delete old receipt if exists
            if (expense.getReceiptImage() != null) {
                Files.deleteIfExists(Paths.get(uploadDir, expense.getReceiptImage()));
            }

            // Save new receipt
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(uploadDir, fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            expense.setReceiptImage(fileName);
        }

        expenseDAO.updateExpense(expense);
        response.sendRedirect(request.getContextPath() + "/expenses?action=view&id=" + expenseId);
    }

    private void handleDeleteExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {
        int expenseId = Integer.parseInt(request.getParameter("id"));
        Expense expense = expenseDAO.getExpenseById(expenseId);

        if (expense == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (expense.getPaidBy().getUserId() != user.getUserId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Delete receipt file if exists
        if (expense.getReceiptImage() != null) {
            String uploadDir = getServletContext().getRealPath("/") + "uploads/receipts/";
            Files.deleteIfExists(Paths.get(uploadDir, expense.getReceiptImage()));
        }

        expenseDAO.deleteExpense(expenseId);
        response.sendRedirect(request.getContextPath() + "/expenses?action=list");
    }

    private void handleSettleExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {
        int expenseId = Integer.parseInt(request.getParameter("id"));
        Expense expense = expenseDAO.getExpenseById(expenseId);

        if (expense == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int userId = user.getUserId();
        int groupId = Integer.parseInt(request.getParameter("groupId"));

        // Calculate amounts for the settlement
        double amountOwed = expenseDAO.getAmountOwedByUser(groupId, userId);
        double amountToReceive = expenseDAO.getAmountOwedToUser(groupId, userId);

        if (amountOwed <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No amount to settle");
            return;
        }

        // Process the settlement logic here
        // You might want to create a new transaction or mark expenses as settled

        response.sendRedirect(request.getContextPath() + "/expenses?action=list&groupId=" + groupId);
    }

    private String getFileExtension(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                String filename = token.substring(token.indexOf("=") + 2, token.length() - 1);
                int dotIndex = filename.lastIndexOf('.');
                return dotIndex > 0 ? filename.substring(dotIndex) : "";
            }
        }
        return "";
    }
}