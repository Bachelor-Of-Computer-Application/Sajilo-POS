package com.possystem.sajilopos.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    Config.get("db.url"),
                    Config.get("db.username"),
                    Config.get("db.password")
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}