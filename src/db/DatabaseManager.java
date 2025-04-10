/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Database Manager - Handles database connections and SQL operations
 * 
 * @author [YOUR NAME HERE]
 */
package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages database connections and operations.
 * Used for Parts I and II of the assignment.
 */
public class DatabaseManager {
    // Database connection parameters
    // School server connection (commented out)
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
    // private static final String DB_USER = "your_db_username"; // Replace with actual username
    // private static final String DB_PASSWORD = "your_db_password"; // Replace with actual password
    
    // Local MySQL connection
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "Password_27"; 
    
    private Connection connection = null;
    
    /**
     * Establishes a connection to the database.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean connect() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Closes the database connection.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates all required database tables.
     * Part I of the assignment.
     */
    public void createTables() {
        if (connection == null) {
            System.err.println("No database connection");
            return;
        }
        
        try {
            Statement stmt = connection.createStatement();
            
            // Create PERSON table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS PERSON (" +
                "loginId VARCHAR(12) NOT NULL, " +
                "email VARCHAR(50) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "dateCreated DATE NOT NULL, " +
                "PRIMARY KEY (loginId), " +
                "UNIQUE(email))"
            );
            
            // Create MANAGER table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS MANAGER (" +
                "loginId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (loginId), " +
                "FOREIGN KEY (loginId) REFERENCES PERSON (loginId))"
            );
            
            // Create CHARACTER table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS GAME_CHARACTER (" +
                "name VARCHAR(50) NOT NULL, " +
                "playerId VARCHAR(12) NOT NULL, " +
                "locationId VARCHAR(50), " +
                "maxPoints INT NOT NULL, " +
                "currentPoints INT NOT NULL, " +
                "stamina INT NOT NULL, " +
                "strength INT NOT NULL, " +
                "PRIMARY KEY (name), " +
                "FOREIGN KEY (playerId) REFERENCES PERSON (loginId))"
            );
            
            // Add other table creation statements here
            // ...
            
            System.out.println("Database tables created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
    
    /**
     * Populates the database tables with initial data.
     * Part II of the assignment.
     */
    public void populateTables() {
        if (connection == null) {
            System.err.println("No database connection");
            return;
        }
        
        try {
            // Example of using PreparedStatement for inserting data
            String insertPersonSQL = "INSERT INTO PERSON (loginId, email, password, dateCreated) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertPersonSQL);
            
            // Insert a sample person
            pstmt.setString(1, "user1");
            pstmt.setString(2, "user1@example.com");
            pstmt.setString(3, "password123"); // In a real app, this would be hashed
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            
            // Insert sample characters
            String insertCharacterSQL = "INSERT INTO GAME_CHARACTER " +
                "(name, playerId, locationId, maxPoints, currentPoints, stamina, strength) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(insertCharacterSQL);
            
            // Insert a sample character
            pstmt.setString(1, "Warrior");
            pstmt.setString(2, "user1");
            pstmt.setString(3, "default");
            pstmt.setInt(4, 150);
            pstmt.setInt(5, 150);
            pstmt.setInt(6, 25);
            pstmt.setInt(7, 30);
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Silently ignore duplicate key errors when populating initial data
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
            // Add more data insertion statements here
            // ...
            
            System.out.println("Database tables populated successfully");
        } catch (SQLException e) {
            System.err.println("Error populating tables: " + e.getMessage());
        }
    }
    
    /**
     * Main method for testing database operations.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        
        if (dbManager.connect()) {
            System.out.println("Connected to the database");
            
            // Create database tables
            dbManager.createTables();
            
            // Populate tables with initial data
            dbManager.populateTables();
            
            // Disconnect from the database
            dbManager.disconnect();
            System.out.println("Disconnected from the database");
        } else {
            System.out.println("Failed to connect to the database");
        }
    }
} 