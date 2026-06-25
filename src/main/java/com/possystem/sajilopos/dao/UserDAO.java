package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for User operations
 * Handles company-based authentication and user management
 */
public class UserDAO {

    /**
     * Login with company code, username, and password
     * Uses BCrypt password verification
     *
     * @param companyCode Company code (e.g., GPS001)
     * @param username    Username
     * @param password    Plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String companyCode, String username, String password) {
        String sql = "SELECT u.user_id, u.company_id, u.username, u.password_hash, u.role, u.created_at " +
                     "FROM users u " +
                     "INNER JOIN companies c ON u.company_id = c.company_id " +
                     "WHERE c.company_code = ? AND u.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyCode);
            stmt.setString(2, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Verify password using BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getInt("company_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                } else {
                    System.err.println("Password verification failed for user: " + username);
                }
            } else {
                System.err.println("User not found: " + username + " in company: " + companyCode);
            }

        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, company_id, username, password_hash, role, created_at " +
                     "FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get all users for a specific company
     */
    public List<User> getUsersByCompany(int companyId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, company_id, username, password_hash, role, created_at " +
                     "FROM users WHERE company_id = ? ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users by company: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Create new user with BCrypt hashed password
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (company_id, username, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getCompanyId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getRole());
            stmt.setInt(3, user.getUserId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete user
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Hash password using BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    public boolean changePassword(int userId, String newPasswordHash) {
    String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, newPasswordHash);
        stmt.setInt(2, userId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error changing password: " + e.getMessage());
    }
    return false;
}

}
