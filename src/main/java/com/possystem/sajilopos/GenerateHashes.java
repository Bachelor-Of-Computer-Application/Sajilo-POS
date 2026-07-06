package com.possystem.sajilopos;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Generate fresh BCrypt hashes for the database
 */
public class GenerateHashes {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("BCRYPT HASH GENERATOR");
        System.out.println("========================================\n");

        String[] users = {
            "admin:admin123:ADMIN",
            "manager:manager123:MANAGER",
            "cashier:cashier123:CASHIER"
        };

        System.out.println("Generating fresh BCrypt hashes...\n");

        for (String userInfo : users) {
            String[] parts = userInfo.split(":");
            String username = parts[0];
            String password = parts[1];
            // String role = parts[2];  // Role info stored in database directly

            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Hash: " + hash);
            System.out.println();
        }

        System.out.println("========================================");
        System.out.println("SQL UPDATE STATEMENTS");
        System.out.println("========================================\n");

        System.out.println("Run these SQL commands to update your database:\n");

        for (String userInfo : users) {
            String[] parts = userInfo.split(":");
            String username = parts[0];
            String password = parts[1];

            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            
            System.out.println("-- " + username + " / " + password);
            System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE username = '" + username + "';");
            System.out.println();
        }

        System.out.println("========================================");
        System.out.println("Or use this complete INSERT statement:");
        System.out.println("========================================\n");

        System.out.println("DELETE FROM users;");
        for (String userInfo : users) {
            String[] parts = userInfo.split(":");
            String username = parts[0];
            String password = parts[1];
            String role = parts[2];

            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            
            System.out.println("INSERT INTO users (company_id, username, password_hash, role) VALUES (1, '" + username + "', '" + hash + "', '" + role + "');");
        }
    }
}
