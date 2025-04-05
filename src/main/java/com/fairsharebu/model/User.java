package com.fairsharebu.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the FairShareBU system.
 */
public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String profilePicture;
    private LocalDateTime registrationDate;
    private boolean isActive;

    // Relationships
    private List<Group> groups;
    private List<Expense> expensesCreated;
    private List<Expense> expensesParticipated;

    // Default constructor
    public User() {
        this.registrationDate = LocalDateTime.now();
        this.isActive = true;
        this.groups = new ArrayList<>();
        this.expensesCreated = new ArrayList<>();
        this.expensesParticipated = new ArrayList<>();
    }

    // Parameterized constructor
    public User(String username, String email, String password, String fullName, String phoneNumber) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Expense> getExpensesCreated() {
        return expensesCreated;
    }

    public void setExpensesCreated(List<Expense> expensesCreated) {
        this.expensesCreated = expensesCreated;
    }

    public List<Expense> getExpensesParticipated() {
        return expensesParticipated;
    }

    public void setExpensesParticipated(List<Expense> expensesParticipated) {
        this.expensesParticipated = expensesParticipated;
    }

    public void addGroup(Group group) {
        if (this.groups == null) {
            this.groups = new ArrayList<>();
        }
        this.groups.add(group);
    }

    public void addExpenseCreated(Expense expense) {
        if (this.expensesCreated == null) {
            this.expensesCreated = new ArrayList<>();
        }
        this.expensesCreated.add(expense);
    }

    public void addExpenseParticipated(Expense expense) {
        if (this.expensesParticipated == null) {
            this.expensesParticipated = new ArrayList<>();
        }
        this.expensesParticipated.add(expense);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                '}';
    }
}