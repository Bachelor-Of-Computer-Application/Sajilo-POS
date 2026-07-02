package com.possystem.sajilopos.service;

import org.mindrot.jbcrypt.BCrypt;
import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.UserDAO;
import com.possystem.sajilopos.model.User;

/**
 * AuthService - Authentication Service
 * Handles company-based login and session management
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Login with username and password only (no company code).
     */
    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.err.println("Username and password cannot be empty");
            return false;
        }

        try {
            User user = userDAO.loginByUsername(username, password);
            if (user != null) {
                sessionManager.setCurrentUser(user);
                System.out.println("User logged in: " + username + " (" + user.getRole() + ")");
                return true;
            } else {
                System.err.println("Login failed for user: " + username);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Login with company code, username, and password
     *
     * @param companyCode Company code (e.g., GPS001)
     * @param username    Username
     * @param password    Plain text password
     * @return true if login successful, false otherwise
     */
    public boolean login(String companyCode, String username, String password) {
        if (companyCode == null || companyCode.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.err.println("Company code, username, and password cannot be empty");
            return false;
        }

        try {
            User user = userDAO.login(companyCode, username, password);

            if (user != null) {
                sessionManager.setCurrentUser(user);
                System.out.println("User logged in: " + username + " (" + user.getRole() + ") from company: " + companyCode);
                return true;
            } else {
                System.err.println("Login failed for user: " + username + " in company: " + companyCode);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
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

    /**
     * Hash password using BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verify password using BCrypt
     */
    public static boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
