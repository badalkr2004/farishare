package com.fairsharebu.controller;

import com.fairsharebu.dao.*;
import com.fairsharebu.model.Expense;
import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;
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
                // List all expenses that the user is involved in
                List<Expense> userExpenses = expenseDAO.getExpensesWhereUserParticipated(user.getUserId());
                request.setAttribute("expenses", userExpenses);
                request.getRequestDispatcher("/WEB-INF/jsp/expenses/list.jsp").forward(request, response);
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
                    double owes = expenseDAO.calculateUserOwes(groupId, member.getUserId());
                    double isOwed = expenseDAO.calculateUserIsOwed(groupId, member.getUserId());
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
                double payerOwes = expenseDAO.calculateUserOwes(groupId, payerId);

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
                // Create a new expense
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

                // Get the group
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

                // Create the expense
                Expense expense = new Expense();
                expense.setDescription(description);
                expense.setAmount(amount);
                expense.setPaidBy(user);
                expense.setGroup(group);
                expense.setPaymentMethod(paymentMethod);
                expense.setStatus("PENDING");

                // Handle receipt image upload if exists
                Part filePart = request.getPart("receiptImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = UUID.randomUUID().toString() + getFileExtension(filePart);
                    String uploadDir = getServletContext().getRealPath("/") + "uploads/receipts/";

                    // Create directories if they don't exist
                    Files.createDirectories(Paths.get(uploadDir));

                    // Save the file
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, Paths.get(uploadDir, fileName), StandardCopyOption.REPLACE_EXISTING);
                    }

                    expense.setReceiptImage(fileName);
                }

                // Process the expense participants based on the split method
                if ("equal".equals(splitMethod)) {
                    // Equal split among selected participants
                    String[] participantIds = request.getParameterValues("participants");

                    if (participantIds != null && participantIds.length > 0) {
                        List<User> participants = new ArrayList<>();
                        for (String participantId : participantIds) {
                            User participant = userDAO.get(Integer.parseInt(participantId));
                            if (participant != null) {
                                participants.add(participant);
                                expense.addParticipant(participant);
                            }
                        }

                        // Split equally
                        expense.splitEqually();
                    } else {
                        // If no participants selected, split among all group members
                        for (User member : members) {
                            expense.addParticipant(member);
                        }
                        expense.splitEqually();
                    }
                } else if ("percentage".equals(splitMethod)) {
                    // Percentage split
                    Map<User, Double> percentages = new HashMap<>();

                    for (User member : members) {
                        String percentParam = request.getParameter("percent_" + member.getUserId());
                        if (percentParam != null && !percentParam.isEmpty()) {
                            double percent = Double.parseDouble(percentParam);
                            if (percent > 0) {
                                percentages.put(member, percent);
                                expense.addParticipant(member);
                            }
                        }
                    }

                    expense.splitByPercentage(percentages);
                } else if ("amount".equals(splitMethod)) {
                    // Exact amount split
                    Map<User, Double> specificAmounts = new HashMap<>();

                    for (User member : members) {
                        String amountParam = request.getParameter("amount_" + member.getUserId());
                        if (amountParam != null && !amountParam.isEmpty()) {
                            double memberAmount = Double.parseDouble(amountParam);
                            if (memberAmount > 0) {
                                specificAmounts.put(member, memberAmount);
                                expense.addParticipant(member);
                            }
                        }
                    }

                    expense.splitByAmount(specificAmounts);
                }

                // Save the expense to the database
                Expense createdExpense = expenseDAO.createExpense(expense);

                // Create notifications for all participants
                notificationDAO.createExpenseNotification(createdExpense.getExpenseId(), null);

                response.sendRedirect(request.getContextPath() + "/expenses/view/" + createdExpense.getExpenseId());
            } else if (pathInfo.startsWith("/edit/")) {
                // Update an existing expense
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

                String description = request.getParameter("description");
                double amount = Double.parseDouble(request.getParameter("amount"));
                String paymentMethod = request.getParameter("paymentMethod");

                if (description == null || description.trim().isEmpty()) {
                    request.setAttribute("error", "Description is required");
                    request.setAttribute("expense", expense);
                    request.getRequestDispatcher("/WEB-INF/jsp/expenses/edit.jsp").forward(request, response);
                    return;
                }

                expense.setDescription(description);
                expense.setAmount(amount);
                expense.setPaymentMethod(paymentMethod);

                // Handle receipt image upload if exists
                Part filePart = request.getPart("receiptImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = UUID.randomUUID().toString() + getFileExtension(filePart);
                    String uploadDir = getServletContext().getRealPath("/") + "uploads/receipts/";

                    // Create directories if they don't exist
                    Files.createDirectories(Paths.get(uploadDir));

                    // Save the file
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, Paths.get(uploadDir, fileName), StandardCopyOption.REPLACE_EXISTING);
                    }

                    // Delete old receipt image if exists
                    if (expense.getReceiptImage() != null && !expense.getReceiptImage().isEmpty()) {
                        File oldFile = new File(uploadDir, expense.getReceiptImage());
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }

                    expense.setReceiptImage(fileName);
                }

                // Update the expense in the database
                expenseDAO.updateExpense(expense);

                response.sendRedirect(request.getContextPath() + "/expenses/view/" + expense.getExpenseId());
            } else if (pathInfo.equals("/settle")) {
                // Process a settlement
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                int payerId = Integer.parseInt(request.getParameter("payerId"));
                int receiverId = Integer.parseInt(request.getParameter("receiverId"));
                double amount = Double.parseDouble(request.getParameter("amount"));
                String paymentMethod = request.getParameter("paymentMethod");

                // Check if the current user is the payer
                if (payerId != user.getUserId()) {
                    request.setAttribute("errorMessage", "You can only settle your own payments.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Get the group
                Group group = groupDAO.getGroupById(groupId);

                if (group == null) {
                    request.setAttribute("errorMessage", "Group not found.");
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

                // Create a settlement expense (marked as SETTLED)
                Expense settlement = new Expense();
                settlement.setDescription("Settlement payment");
                settlement.setAmount(amount);
                settlement.setPaidBy(payer);
                settlement.setGroup(group);
                settlement.setPaymentMethod(paymentMethod);
                settlement.setStatus("SETTLED");

                // Add the receiver as the only participant with full amount
                settlement.addParticipant(receiver);
                settlement.setShare(receiver, amount);

                // Save the settlement to the database
                Expense createdSettlement = expenseDAO.createExpense(settlement);

                // Mark the participants as paid for relevant expenses
                // This is a simplified version; in a real application,
                // you might want to track which specific expense amounts were settled
                List<Expense> expenses = expenseDAO.getExpensesByGroup(groupId);
                for (Expense expense : expenses) {
                    if (expense.getPaidBy().getUserId() == receiverId &&
                            expense.getParticipants().stream().anyMatch(p -> p.getUserId() == payerId)) {

                        expenseDAO.updateParticipantPaidStatus(expense.getExpenseId(), payerId, true);
                    }
                }

                // Create a notification for the receiver
                String message = payer.getFullName() + " has settled payment of ₹" + amount + " in group "
                        + group.getName();
                notificationDAO.createGroupNotification(groupId, message,
                        "/expenses/view/" + createdSettlement.getExpenseId(), payerId);

                response.sendRedirect(request.getContextPath() + "/expenses/group?id=" + groupId);
            } else if (pathInfo.equals("/delete")) {
                // Delete an expense
                int expenseId = Integer.parseInt(request.getParameter("expenseId"));
                Expense expense = expenseDAO.getExpenseById(expenseId);

                if (expense == null) {
                    request.setAttribute("errorMessage", "Expense not found.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Check if the user is the one who paid the expense
                if (expense.getPaidBy().getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "You are not authorized to delete this expense.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                // Delete the expense from the database
                expenseDAO.deleteExpense(expenseId);

                // Delete receipt image if exists
                if (expense.getReceiptImage() != null && !expense.getReceiptImage().isEmpty()) {
                    String uploadDir = getServletContext().getRealPath("/") + "uploads/receipts/";
                    File receiptFile = new File(uploadDir, expense.getReceiptImage());
                    if (receiptFile.exists()) {
                        receiptFile.delete();
                    }
                }

                // Redirect to the group expenses page
                int groupId = expense.getGroup().getGroupId();
                response.sendRedirect(request.getContextPath() + "/expenses/group?id=" + groupId);
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

    private String getFileExtension(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");

        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                String fileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
                int dotIndex = fileName.lastIndexOf(".");
                return (dotIndex > 0) ? fileName.substring(dotIndex) : "";
            }
        }

        return "";
    }
}