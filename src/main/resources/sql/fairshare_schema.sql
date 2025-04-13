-- FairShareBU - Complete Database Schema
-- This file contains all table definitions for the FairShareBU application
-- Execute this script to set up your database from scratch

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS fairsharebu;

-- Use the database
USE fairsharebu;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullName VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(20),
    profilePicture VARCHAR(255),
    registrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isActive BOOLEAN DEFAULT TRUE
);

-- Create groups table
CREATE TABLE IF NOT EXISTS groups (
    groupId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    creatorId INT NOT NULL,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    groupImage VARCHAR(255),
    location VARCHAR(100),
    privateGroup BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (creatorId) REFERENCES users(userId)
);

-- Create group_members junction table
CREATE TABLE IF NOT EXISTS group_members (
    groupId INT NOT NULL,
    userId INT NOT NULL,
    joinedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (groupId, userId),
    FOREIGN KEY (groupId) REFERENCES groups(groupId),
    FOREIGN KEY (userId) REFERENCES users(userId)
);

-- Create expenses table
CREATE TABLE IF NOT EXISTS expenses (
    expenseId INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    paid_by INT NOT NULL,
    group_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    receipt_image VARCHAR(255),
    payment_method VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (paid_by) REFERENCES users(userId),
    FOREIGN KEY (group_id) REFERENCES groups(groupId)
);

-- Create expense_participants junction table
CREATE TABLE IF NOT EXISTS expense_participants (
    expense_id INT NOT NULL,
    user_id INT NOT NULL,
    share_amount DECIMAL(10, 2) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (expense_id, user_id),
    FOREIGN KEY (expense_id) REFERENCES expenses(expenseId),
    FOREIGN KEY (user_id) REFERENCES users(userId)
);

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    messageId INT AUTO_INCREMENT PRIMARY KEY,
    senderId INT NOT NULL,
    groupId INT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (senderId) REFERENCES users(userId),
    FOREIGN KEY (groupId) REFERENCES groups(groupId)
);

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    payer_id INT NOT NULL,
    receiver_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    group_id INT NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (payer_id) REFERENCES users(userId),
    FOREIGN KEY (receiver_id) REFERENCES users(userId),
    FOREIGN KEY (group_id) REFERENCES groups(groupId)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    link VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(userId)
);

-- Optional: Insert sample data for testing
-- Uncomment the following section to add sample data

/*
-- Insert sample users (password is 'password' - hashed)
INSERT INTO users (username, email, password, fullName, phoneNumber)
VALUES 
('john', 'john@example.com', '$2a$10$Yg.XLAv3rGkYvZnYXsWqPOpCPVWoDlCvYLlL6JAujjsmEgLcGD1Iq', 'John Doe', '1234567890'),
('sarah', 'sarah@example.com', '$2a$10$Yg.XLAv3rGkYvZnYXsWqPOpCPVWoDlCvYLlL6JAujjsmEgLcGD1Iq', 'Sarah Smith', '9876543210'),
('mike', 'mike@example.com', '$2a$10$Yg.XLAv3rGkYvZnYXsWqPOpCPVWoDlCvYLlL6JAujjsmEgLcGD1Iq', 'Mike Johnson', '5555555555');

-- Insert sample group
INSERT INTO groups (name, description, creatorId, location)
VALUES ('Lunch Buddies', 'For splitting lunch bills', 1, 'Restaurant');

-- Add members to the group
INSERT INTO group_members (groupId, userId)
VALUES (1, 1), (1, 2), (1, 3);

-- Insert sample expense
INSERT INTO expenses (description, amount, paid_by, group_id, payment_method)
VALUES ('Thursday Lunch', 450.00, 1, 1, 'Cash');

-- Add expense participants and their shares
INSERT INTO expense_participants (expense_id, user_id, share_amount)
VALUES (1, 1, 150.00), (1, 2, 150.00), (1, 3, 150.00);

-- Insert sample payment
INSERT INTO payments (payer_id, receiver_id, amount, description, group_id, status)
VALUES (2, 1, 150.00, 'Paying for Thursday Lunch', 1, 'COMPLETED');

-- Insert sample notification
INSERT INTO notifications (user_id, title, message, link)
VALUES 
(1, 'Payment Received', 'Sarah Smith sent you ₹150.00 for Thursday Lunch', '/payments/view?id=1'),
(2, 'Payment Sent', 'You sent ₹150.00 to John Doe for Thursday Lunch', '/payments/view?id=1');
*/ 