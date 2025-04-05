-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS fairsharebu;

-- Use the database
USE fairsharebu;

-- Create users table
CREATE TABLE IF NOT EXISTS `users` (
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
CREATE TABLE IF NOT EXISTS `groups` (
    groupId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    creatorId INT NOT NULL,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    groupImage VARCHAR(255),
    location VARCHAR(100),
    FOREIGN KEY (creatorId) REFERENCES `users`(userId)
);

-- Create group_members junction table
CREATE TABLE IF NOT EXISTS `group_members` (
    groupId INT NOT NULL,
    userId INT NOT NULL,
    joinedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (groupId, userId),
    FOREIGN KEY (groupId) REFERENCES `groups`(groupId),
    FOREIGN KEY (userId) REFERENCES `users`(userId)
);

-- Create expenses table
CREATE TABLE IF NOT EXISTS `expenses` (
    expenseId INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    paidById INT NOT NULL,
    groupId INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    receiptImage VARCHAR(255),
    paymentMethod VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (paidById) REFERENCES `users`(userId),
    FOREIGN KEY (groupId) REFERENCES `groups`(groupId)
);

-- Create expense_participants junction table
CREATE TABLE IF NOT EXISTS `expense_participants` (
    expenseId INT NOT NULL,
    userId INT NOT NULL,
    share DECIMAL(10, 2) NOT NULL,
    isPaid BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (expenseId, userId),
    FOREIGN KEY (expenseId) REFERENCES `expenses`(expenseId),
    FOREIGN KEY (userId) REFERENCES `users`(userId)
);

-- Create messages table
CREATE TABLE IF NOT EXISTS `messages` (
    messageId INT AUTO_INCREMENT PRIMARY KEY,
    senderId INT NOT NULL,
    groupId INT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (senderId) REFERENCES `users`(userId),
    FOREIGN KEY (groupId) REFERENCES `groups`(groupId)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS `notifications` (
    notificationId INT AUTO_INCREMENT PRIMARY KEY,
    recipientId INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    link VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (recipientId) REFERENCES `users`(userId)
);

-- Insert some sample data (optional)
-- Uncomment the following lines to add sample data

/*
-- Insert sample users (password is 'password')
INSERT INTO `users` (username, email, password, fullName, phoneNumber)
VALUES 
('john', 'john@example.com', '$2a$10$Yg.XLAv3rGkYvZnYXsWqPOpCPVWoDlCvYLlL6JAujjsmEgLcGD1Iq', 'John Doe', '1234567890'),
('sarah', 'sarah@example.com', '$2a$10$Yg.XLAv3rGkYvZnYXsWqPOpCPVWoDlCvYLlL6JAujjsmEgLcGD1Iq', 'Sarah Smith', '9876543210'),
('mike', 'mike@example.com', '$2a$10$Yg.XLAv3rGkYvZnYXsWqPOpCPVWoDlCvYLlL6JAujjsmEgLcGD1Iq', 'Mike Johnson', '5555555555');

-- Insert sample group
INSERT INTO `groups` (name, description, creatorId, location)
VALUES ('Lunch Buddies', 'For splitting lunch bills at The House of Chow', 1, 'The House of Chow');

-- Add members to the group
INSERT INTO `group_members` (groupId, userId)
VALUES (1, 1), (1, 2), (1, 3);

-- Insert sample expense
INSERT INTO `expenses` (description, amount, paidById, groupId, paymentMethod)
VALUES ('Thursday Lunch', 450.00, 1, 1, 'Cash');

-- Add expense participants and their shares
INSERT INTO `expense_participants` (expenseId, userId, share)
VALUES (1, 1, 150.00), (1, 2, 150.00), (1, 3, 150.00);

-- Insert sample message
INSERT INTO `messages` (senderId, groupId, content)
VALUES (1, 1, 'I added the expense for our lunch today.');

-- Insert sample notification
INSERT INTO `notifications` (recipientId, type, message, link)
VALUES 
(2, 'EXPENSE_ADDED', 'John Doe added an expense "Thursday Lunch" of ₹450.00 in group Lunch Buddies', '/expense?id=1'),
(3, 'EXPENSE_ADDED', 'John Doe added an expense "Thursday Lunch" of ₹450.00 in group Lunch Buddies', '/expense?id=1');
*/ 