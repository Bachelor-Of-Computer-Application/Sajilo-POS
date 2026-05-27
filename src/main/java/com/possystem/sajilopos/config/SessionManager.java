package com.possystem.sajilopos.config;

import com.possystem.sajilopos.model.User;


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
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRoleName());
    }

   
    public boolean isManager() {
        return currentUser != null && "MANAGER".equalsIgnoreCase(currentUser.getRoleName());
    }

    public boolean isCashier() {
        return currentUser != null && "CASHIER".equalsIgnoreCase(currentUser.getRoleName());
    }

   
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRoleName() : null;
    }

   
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }

    public void logout() {
        currentUser = null;
    }
}
