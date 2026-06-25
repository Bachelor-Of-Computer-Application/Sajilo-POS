package com.possystem.sajilopos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database Connection Manager
 *
 * Uses HikariCP for efficient connection pooling.
 * Connection details are loaded from config.properties:
 * - db.url
 * - db.username
 * - db.password
 */
public class DBConnection {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(Config.get("db.url"));
            config.setUsername(Config.get("db.username"));
            config.setPassword(Config.get("db.password"));

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);

            System.out.println("HikariCP connection pool initialized");

        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Connection pool not initialized");
        }
        return dataSource.getConnection();
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