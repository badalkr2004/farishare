package com.fairsharebu.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group in the FairShareBU system.
 * A group contains multiple users who share expenses together.
 */
public class Group {
    private int groupId;
    private String name;
    private String description;
    private User creator;
    private LocalDateTime createdDate;
    private String groupImage;
    private String location; // e.g., "The House of Chow", "Snapeats"

    // Relationships
    private List<User> members;
    private List<Expense> expenses;
    private List<Message> messages;

    // Default constructor
    public Group() {
        this.createdDate = LocalDateTime.now();
        this.members = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    // Parameterized constructor
    public Group(String name, String description, User creator, String location) {
        this();
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.location = location;
        this.members.add(creator); // Creator is automatically a member
    }

    // Getters and Setters
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // Helper methods
    public void addMember(User user) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        if (!members.contains(user)) {
            this.members.add(user);
            user.addGroup(this);
        }
    }

    public void addExpense(Expense expense) {
        if (this.expenses == null) {
            this.expenses = new ArrayList<>();
        }
        this.expenses.add(expense);
    }

    public void addMessage(Message message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
    }

    public double getTotalExpenses() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + (creator != null ? creator.getUsername() : "null") +
                ", createdDate=" + createdDate +
                ", location='" + location + '\'' +
                ", memberCount=" + (members != null ? members.size() : 0) +
                '}';
    }
}