package com.possystem.sajilopos;

import com.possystem.sajilopos.config.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates or updates demo users for testing role-based login.
 */
public class SeedRoleUsers {

    private static final String DEFAULT_COMPANY_CODE = "GPS001";
    private static final String DEFAULT_COMPANY_NAME = "Demo Company";

    private static final DemoUser[] DEMO_USERS = {
            new DemoUser("admin", "admin123", "ADMIN"),
            new DemoUser("manager", "manager123", "MANAGER"),
            new DemoUser("cashier", "cashier123", "CASHIER")
    };

    public static void main(String[] args) {
        String companyCode = args.length > 0 ? args[0].trim() : DEFAULT_COMPANY_CODE;
        String companyName = args.length > 1 ? args[1].trim() : DEFAULT_COMPANY_NAME;

        if (companyCode.isEmpty()) {
            companyCode = DEFAULT_COMPANY_CODE;
        }
        if (companyName.isEmpty()) {
            companyName = DEFAULT_COMPANY_NAME;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Database connection failed. Check config.properties and MySQL.");
                return;
            }

            int companyId = ensureCompany(conn, companyCode, companyName);
            for (DemoUser user : DEMO_USERS) {
                upsertUser(conn, companyId, user);
            }

            System.out.println("Role test users are ready.");
            System.out.println("Company Code: " + companyCode);
            for (DemoUser user : DEMO_USERS) {
                System.out.println(user.role + ": username=" + user.username + ", password=" + user.password);
            }
        } catch (SQLException e) {
            System.err.println("Failed to seed role users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int ensureCompany(Connection conn, String companyCode, String companyName) throws SQLException {
        String selectSql = "SELECT company_id FROM companies WHERE company_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, companyCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("company_id");
                }
            }
        }

        String insertSql = "INSERT INTO companies (company_name, company_code) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, companyName);
            stmt.setString(2, companyCode);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Company was inserted, but no generated company_id was returned.");
    }

    private static void upsertUser(Connection conn, int companyId, DemoUser user) throws SQLException {
        String passwordHash = BCrypt.hashpw(user.password, BCrypt.gensalt());
        Integer existingUserId = findUserId(conn, companyId, user.username);

        if (existingUserId == null) {
            String insertSql = "INSERT INTO users (company_id, username, password_hash, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, companyId);
                stmt.setString(2, user.username);
                stmt.setString(3, passwordHash);
                stmt.setString(4, user.role);
                stmt.executeUpdate();
            }
            return;
        }

        String updateSql = "UPDATE users SET password_hash = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, passwordHash);
            stmt.setString(2, user.role);
            stmt.setInt(3, existingUserId);
            stmt.executeUpdate();
        }
    }

    private static Integer findUserId(Connection conn, int companyId, String username) throws SQLException {
        String selectSql = "SELECT user_id FROM users WHERE company_id = ? AND username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, companyId);
            stmt.setString(2, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }

        return null;
    }

    private static class DemoUser {
        private final String username;
        private final String password;
        private final String role;

        private DemoUser(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }
}
