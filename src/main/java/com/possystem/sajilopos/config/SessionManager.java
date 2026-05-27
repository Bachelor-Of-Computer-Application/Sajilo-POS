package com.possystem.sajilopos.config;

import com.possystem.sajilopos.model.User;

/**
 * Singleton class to manage the current user session
 * This tracks who is logged in throughout the application
 */
public class SessionManager {

    private static SessionManager instance = null;
    private User currentUser = null;

    /**
     * Private constructor to prevent instantiation
     */
    private SessionManager() {
    }

    /**
     * Get the singleton instance of SessionManager
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set the current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Get the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if the current user is an ADMIN
     */
    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRoleName());
    }

    /**
     * Check if the current user is a MANAGER
     */
    public boolean isManager() {
        return currentUser != null && "MANAGER".equalsIgnoreCase(currentUser.getRoleName());
    }

    /**
     * Check if the current user is a CASHIER
     */
    public boolean isCashier() {
        return currentUser != null && "CASHIER".equalsIgnoreCase(currentUser.getRoleName());
    }

    /**
     * Get current user's role name
     */
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRoleName() : null;
    }

    /**
     * Get current user's full name
     */
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }

    /**
     * Logout - clear the current user
     */
    public void logout() {
        currentUser = null;
    }
}
