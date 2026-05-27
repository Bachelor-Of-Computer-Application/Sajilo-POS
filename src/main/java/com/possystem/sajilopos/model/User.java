package com.possystem.sajilopos.model;

public class User {
    private int userId;
    private String fullName;
    private String username;
    private String passwordHash;
    private int roleId;
    private String roleName;
    private boolean active;

    public User(int userId, String fullName, String username, String passwordHash, int roleId, boolean active) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.active = active;
    }

    
    public User(int userId, String fullName, String username, String passwordHash, String roleName, boolean active) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleName = roleName;
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isActive() {
        return active;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}