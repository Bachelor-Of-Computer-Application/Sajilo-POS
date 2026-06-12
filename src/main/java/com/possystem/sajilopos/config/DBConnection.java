package com.possystem.sajilopos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL Database Connection Utility
 * Connects to: jdbc:mysql://localhost:3306/general_pos
 */
public class DBConnection {

    public static Connection getConnection() {
        try {
            String url = Config.get("db.url");
            String username = Config.get("db.username");
            String password = Config.get("db.password");

            if (url == null || url.trim().isEmpty()) {
                System.err.println("Database configuration missing in config.properties");
                return null;
            }

            // MySQL connection with credentials
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("MySQL database connection established successfully");
            return conn;

        } catch (SQLException e) {
            System.err.println("MySQL database connection failed: " + e.getMessage());
            System.err.println("Check your config.properties and ensure MySQL server is running");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error during database connection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
