package com.fairsharebu.model;

import java.time.LocalDateTime;

/**
 * Represents a message in the chat functionality of the FairShareBU system.
 */
public class Message {
    private int messageId;
    private User sender;
    private Group group;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    // Default constructor
    public Message() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    // Parameterized constructor
    public Message(User sender, Group group, String content) {
        this();
        this.sender = sender;
        this.group = group;
        this.content = content;

        // Add message to the group
        if (group != null) {
            group.addMessage(this);
        }
    }

    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Mark the message as read.
     */
    public void markAsRead() {
        this.isRead = true;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", sender=" + (sender != null ? sender.getUsername() : "null") +
                ", group=" + (group != null ? group.getName() : "null") +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}