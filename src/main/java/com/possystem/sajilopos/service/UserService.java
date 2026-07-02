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

    /**
     * Any logged-in user can update their own username + password.
     * Requires current password to verify identity.
     */
    public boolean updateOwnAccount(String currentPassword, String newUsername, String newPassword) {
        requireLogin();

        User current = sessionManager.getCurrentUser();

        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalArgumentException("Current password is required to update your account.");
        }
        if (!BCrypt.checkpw(currentPassword, current.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("New username cannot be empty.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }

        String hashedNew = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean success = userDAO.updateOwnAccount(current.getUserId(), newUsername.trim(), hashedNew);

        if (success) {
            // Refresh session with updated username
            current.setUsername(newUsername.trim());
            current.setPasswordHash(hashedNew);
        }

        return success;
    }

    /**
     * Any logged-in user can change their own password.
     * Must provide the correct current password first.
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        requireLogin();

        User current = sessionManager.getCurrentUser();

        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalArgumentException("Current password cannot be empty.");
        }
        if (!BCrypt.checkpw(currentPassword, current.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password must be different from the current password.");
        }

        String hashedNew = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean success = userDAO.updatePassword(current.getUserId(), hashedNew);

        if (success) {
            // Refresh session hash so subsequent checks work
            current.setPasswordHash(hashedNew);
        }

        return success;
    }

    /**
     * ADMIN only: update the role of any user in the same company.
     * Cannot change your own role.
     */
    public boolean updateUserRole(int userId, String newRole) {
        requireAdmin();

        if (!newRole.equals("ADMIN") && !newRole.equals("MANAGER") && !newRole.equals("CASHIER")) {
            throw new IllegalArgumentException("Invalid role. Must be ADMIN, MANAGER, or CASHIER.");
        }
        if (sessionManager.getCurrentUser().getUserId() == userId) {
            throw new IllegalStateException("You cannot change your own role.");
        }

        User target = userDAO.getUserById(userId);
        if (target == null) {
            throw new IllegalArgumentException("User not found.");
        }
        // Ensure target belongs to same company
        if (target.getCompanyId() != sessionManager.getCurrentCompanyId()) {
            throw new IllegalStateException("Cannot modify a user from a different company.");
        }

        return userDAO.updateRole(userId, newRole);
    }

    public boolean deleteUser(int userId) {
        requireAdmin();
        // Prevent admin from deleting their own account
        if (sessionManager.getCurrentUser().getUserId() == userId) {
            throw new IllegalStateException("Cannot delete your own account");
        }

        return userDAO.deleteUser(userId);
    }

    /**
     * ADMIN only: reset any user's password without needing the current password.
     */
    public boolean adminResetPassword(int userId, String newPassword) {
        requireAdmin();

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        User target = userDAO.getUserById(userId);
        if (target == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (target.getCompanyId() != sessionManager.getCurrentCompanyId()) {
            throw new IllegalStateException("Cannot modify a user from a different company.");
        }

        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return userDAO.updatePassword(userId, hashed);
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
