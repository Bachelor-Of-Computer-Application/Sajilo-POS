package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    
    public User getUserByUsername(String username) {
        String sql = "SELECT u.user_id, u.full_name, u.username, u.password_hash, u.role_id, u.active, " +
                "r.role_name FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.role_id WHERE u.username = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role_name"),
                        rs.getBoolean("active"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
            e.printStackTrace();
        }

        return null; 
    }

    
    public User getUserById(int userId) {
        String sql = "SELECT u.user_id, u.full_name, u.username, u.password_hash, u.role_id, u.active, " +
                "r.role_name FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.role_id WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role_name"),
                        rs.getBoolean("active"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id, u.full_name, u.username, u.password_hash, u.role_id, u.active, " +
                "r.role_name FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.role_id ORDER BY u.user_id";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role_name"),
                        rs.getBoolean("active")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (full_name, username, password_hash, role_id, active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setInt(4, user.getRoleId());
            stmt.setBoolean(5, user.isActive());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
    public boolean updateUser(User user) {
    String sql = "UPDATE users SET full_name = ?, username = ?, role_id = ?, active = ? WHERE user_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, user.getFullName());
        stmt.setString(2, user.getUsername());
        stmt.setInt(3, user.getRoleId());
        stmt.setBoolean(4, user.isActive());
        stmt.setInt(5, user.getUserId());
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error updating user: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

public boolean deleteUser(int userId) {
    // Soft delete — just deactivate the account
    String sql = "UPDATE users SET active = 0 WHERE user_id = ?";
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

}
