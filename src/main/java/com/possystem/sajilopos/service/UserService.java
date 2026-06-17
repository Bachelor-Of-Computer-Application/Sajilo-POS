package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.UserDAO;
import com.possystem.sajilopos.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    // --- Permission checks ---

    public void requireAdmin() {
        if (!sessionManager.isAdmin()) {
            throw new IllegalStateException("Access denied. ADMIN role required.");
        }
    }

    public void requireManagerOrAbove() {
        if (!sessionManager.isAdmin() && !sessionManager.isManager()) {
            throw new IllegalStateException("Access denied. MANAGER or ADMIN role required.");
        }
    }

    public void requireLogin() {
        if (!sessionManager.isLoggedIn()) {
            throw new IllegalStateException("Access denied. Please login first.");
        }
    }

    // --- User management (Admin only) ---

    public boolean createUser(String username, String password, String role) {
        requireAdmin();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (!role.equals("ADMIN") && !role.equals("MANAGER") && !role.equals("CASHIER")) {
            throw new IllegalArgumentException("Invalid role. Must be ADMIN, MANAGER or CASHIER");
        }

        int companyId = sessionManager.getCurrentCompanyId();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(companyId, username.trim(), hashedPassword, role);
        return userDAO.createUser(newUser);
    }

    public boolean updateUser(int userId, String username, String role) {
        requireAdmin();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!role.equals("ADMIN") && !role.equals("MANAGER") && !role.equals("CASHIER")) {
            throw new IllegalArgumentException("Invalid role");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setUsername(username.trim());
        user.setRole(role);
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int userId) {
        requireAdmin();

        // Prevent admin from deleting their own account
        if (sessionManager.getCurrentUser().getUserId() == userId) {
            throw new IllegalStateException("Cannot delete your own account");
        }

        return userDAO.deleteUser(userId);
    }

    public List<User> getAllUsers() {
        requireAdmin();
        int companyId = sessionManager.getCurrentCompanyId();
        return userDAO.getUsersByCompany(companyId);
    }

    public User getUserById(int userId) {
        requireAdmin();
        return userDAO.getUserById(userId);
    }
}
