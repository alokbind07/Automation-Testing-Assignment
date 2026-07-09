package com.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
            logger.info("Configuration properties loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load config.properties file", e);
            throw new RuntimeException("Configuration file could not be loaded: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        // 1. Check System properties (e.g., -Dkey=value)
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }

        // 2. Check System environment variables (e.g., key or UPPERCASE_KEY)
        value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        value = System.getenv(key.toUpperCase());
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }

        // 3. Fallback to config.properties
        value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property '{}' not found in System properties, Environment variables, or config.properties", key);
        }
        return value;
    }
}
