package com.possystem.sajilopos.service;

import org.mindrot.jbcrypt.BCrypt;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.UserDAO;
import com.possystem.sajilopos.model.User;


public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();


    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            System.err.println("Username and password cannot be empty");
            return false;
        }

        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                System.err.println("User not found: " + username);
                return false;
            }

            if (!user.isActive()) {
                System.err.println("User account is disabled: " + username);
                return false;
            }

            if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                System.err.println("Invalid password for user: " + username);
                return false;
            }

          
            sessionManager.setCurrentUser(user);
            System.out.println("User logged in: " + username + " (" + user.getRoleName() + ")");
            return true;

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        if (sessionManager.getCurrentUser() != null) {
            System.out.println("User logged out: " + sessionManager.getCurrentUserName());
        }
        sessionManager.logout();
    }

    
    public User getLoggedInUser() {
        return sessionManager.getCurrentUser();
    }

    
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    
    public boolean isAdmin() {
        return sessionManager.isAdmin();
    }

    
    public boolean isManager() {
        return sessionManager.isManager();
    }


    public boolean isCashier() {
        return sessionManager.isCashier();
    }

    
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    
    public static boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
