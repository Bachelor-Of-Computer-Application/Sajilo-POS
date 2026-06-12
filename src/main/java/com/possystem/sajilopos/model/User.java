package com.possystem.sajilopos.model;

import java.time.LocalDateTime;

/**
 * User Model
 * Represents a user in the system with company association
 */
public class User {
    private int userId;
    private int companyId;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime createdAt;

    // Constructor with all fields
    public User(int userId, int companyId, String username, String passwordHash, String role, LocalDateTime createdAt) {
        this.userId = userId;
        this.companyId = companyId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Constructor without timestamp
    public User(int userId, int companyId, String username, String passwordHash, String role) {
        this.userId = userId;
        this.companyId = companyId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Constructor for creating new users (no ID yet)
    public User(int companyId, String username, String passwordHash, String role) {
        this.companyId = companyId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", companyId=" + companyId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
