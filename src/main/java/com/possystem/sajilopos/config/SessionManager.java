package com.possystem.sajilopos.config;

import com.possystem.sajilopos.model.User;

/**
 * Session Manager - Singleton
 * Manages the currently logged-in user session
 */
public class SessionManager {

    private static SessionManager instance = null;
    private User currentUser = null;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isManager() {
        return currentUser != null && "MANAGER".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isCashier() {
        return currentUser != null && "CASHIER".equalsIgnoreCase(currentUser.getRole());
    }

    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public int getCurrentCompanyId() {
        return currentUser != null ? currentUser.getCompanyId() : -1;
    }

    public void logout() {
        currentUser = null;
    }
}
