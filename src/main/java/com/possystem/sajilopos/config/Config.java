package com.possystem.sajilopos.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input =
                     Config.class.getClassLoader()
                             .getResourceAsStream("config.properties")) {

            if (input == null) {
                System.err.println("config.properties not found");
            } else {
                properties.load(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}