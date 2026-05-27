package com.possystem.sajilopos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager
 * Handles MSSQL connection pooling and management
 */
public class DBConnection {

    /**
     * Get a new database connection
     * 
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            String url = Config.get("db.url");
            String username = Config.get("db.username");
            String password = Config.get("db.password");

            if (url == null || username == null || password == null) {
                System.err.println("Database configuration missing in config.properties");
                return null;
            }

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established successfully");
            return conn;

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Check your config.properties and ensure MSSQL server is running");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error during database connection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Test database connectivity
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}