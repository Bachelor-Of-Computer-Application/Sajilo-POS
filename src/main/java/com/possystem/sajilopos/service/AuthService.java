package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.UserDAO;
import com.possystem.sajilopos.model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Authentication Service
 * Handles user login, logout, and role-based access control
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Authenticate user with username and password
     * 
     * @param username User's username
     * @param password User's plain text password
     * @return true if authentication successful, false otherwise
     */
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

            // Verify password using BCrypt
            if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                System.err.println("Invalid password for user: " + username);
                return false;
            }

            // Set the user in session
            sessionManager.setCurrentUser(user);
            System.out.println("User logged in: " + username + " (" + user.getRoleName() + ")");
            return true;

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Logout the current user
     */
    public void logout() {
        if (sessionManager.getCurrentUser() != null) {
            System.out.println("User logged out: " + sessionManager.getCurrentUserName());
        }
        sessionManager.logout();
    }

    /**
     * Get the currently logged-in user
     */
    public User getLoggedInUser() {
        return sessionManager.getCurrentUser();
    }

    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    /**
     * Check if current user is an ADMIN
     */
    public boolean isAdmin() {
        return sessionManager.isAdmin();
    }

    /**
     * Check if current user is a MANAGER
     */
    public boolean isManager() {
        return sessionManager.isManager();
    }

    /**
     * Check if current user is a CASHIER
     */
    public boolean isCashier() {
        return sessionManager.isCashier();
    }

    /**
     * Hash a plain text password using BCrypt
     * Useful for creating new users
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verify if a plain text password matches a hash
     */
    public static boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
