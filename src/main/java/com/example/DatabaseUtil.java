package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for database operations
 */
public class DatabaseUtil {
    // Database configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/email_registration";
    private static final String DB_USER = "email_user";
    private static final String DB_PASSWORD = "vitap12345";
    
    /**
     * Get a connection to the database
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load the PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found! Make sure you've added the dependency to your pom.xml");
            throw e;
        }
        
        // Return connection
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed! Check your connection parameters:");
            System.err.println("URL: " + DB_URL);
            System.err.println("User: " + DB_USER);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test if database connection can be established
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("Database connection error!");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during database connection test!");
            e.printStackTrace();
            return false;
        }
    }
}
