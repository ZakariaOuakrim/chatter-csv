package com.zakaria.projectmanagement.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvLoader {
    private static final Map<String, String> envVars = new HashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) return;
        
        // First try to load from .env file
        try {
            File envFile = new File(".env");
            if (envFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                }
                
                for (String key : props.stringPropertyNames()) {
                    envVars.put(key, props.getProperty(key));
                }
                
                System.out.println("Loaded environment variables from .env file");
            } else {
                System.out.println(".env file not found");
            }
        } catch (IOException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
        
        // Then try to load from system environment variables (these take precedence)
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            envVars.put(entry.getKey(), entry.getValue());
        }
        
        loaded = true;
    }
    
    public static String get(String key) {
        if (!loaded) {
            load();
        }
        return envVars.get(key);
    }
}