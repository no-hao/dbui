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
            
            // Drop the tables if they exist to avoid foreign key constraint issues
            stmt.executeUpdate("DROP TABLE IF EXISTS CREATURESPRESENT");
            stmt.executeUpdate("DROP TABLE IF EXISTS CHARACTERSPRESENT");
            stmt.executeUpdate("DROP TABLE IF EXISTS ITEMSPRESENT");
            stmt.executeUpdate("DROP TABLE IF EXISTS ALLOWEDTOGO");
            stmt.executeUpdate("DROP TABLE IF EXISTS PLAYERHATES");
            stmt.executeUpdate("DROP TABLE IF EXISTS PLAYERLIKES");
            stmt.executeUpdate("DROP TABLE IF EXISTS CREATUREHATES");
            stmt.executeUpdate("DROP TABLE IF EXISTS CREATURELIKES");
            stmt.executeUpdate("DROP TABLE IF EXISTS CREATURE");
            stmt.executeUpdate("DROP TABLE IF EXISTS WEAPON");
            stmt.executeUpdate("DROP TABLE IF EXISTS ABILITY");
            stmt.executeUpdate("DROP TABLE IF EXISTS CONTAINEDIN");
            stmt.executeUpdate("DROP TABLE IF EXISTS CONTAINER");
            stmt.executeUpdate("DROP TABLE IF EXISTS ARMOR");
            stmt.executeUpdate("DROP TABLE IF EXISTS ITEM");
            stmt.executeUpdate("DROP TABLE IF EXISTS GAMECHARACTER");
            stmt.executeUpdate("DROP TABLE IF EXISTS EXITSTO");
            stmt.executeUpdate("DROP TABLE IF EXISTS LOCATION");
            stmt.executeUpdate("DROP TABLE IF EXISTS PLAYER");
            stmt.executeUpdate("DROP TABLE IF EXISTS MODERATOR");
            stmt.executeUpdate("DROP TABLE IF EXISTS MANAGER");
            stmt.executeUpdate("DROP TABLE IF EXISTS PERSON");
            
            // Create PERSON table
            stmt.executeUpdate(
                "CREATE TABLE PERSON (" +
                "loginId VARCHAR(12) NOT NULL, " +
                "email VARCHAR(50) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "dateCreated DATE NOT NULL, " +
                "PRIMARY KEY (loginId), " +
                "UNIQUE(email))"
            );
            
            // Create MANAGER table
            stmt.executeUpdate(
                "CREATE TABLE MANAGER (" +
                "loginId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (loginId), " +
                "FOREIGN KEY (loginId) REFERENCES PERSON (loginId))"
            );
            
            // Create MODERATOR table
            stmt.executeUpdate(
                "CREATE TABLE MODERATOR (" +
                "loginId VARCHAR(12) NOT NULL, " +
                "isSilenced BOOLEAN NOT NULL, " +
                "isBlocked BOOLEAN NOT NULL, " +
                "worksWith VARCHAR(12), " +
                "PRIMARY KEY (loginId), " +
                "FOREIGN KEY (loginId) REFERENCES PERSON (loginId), " +
                "FOREIGN KEY (worksWith) REFERENCES MANAGER (loginId))"
            );
            
            // Create PLAYER table
            stmt.executeUpdate(
                "CREATE TABLE PLAYER (" +
                "loginId VARCHAR(12) NOT NULL, " +
                "isSilenced BOOLEAN NOT NULL, " +
                "isBlocked BOOLEAN NOT NULL, " +
                "watchedBy VARCHAR(12), " +
                "PRIMARY KEY (loginId), " +
                "FOREIGN KEY (loginId) REFERENCES PERSON (loginId), " +
                "FOREIGN KEY (watchedBy) REFERENCES PERSON (loginId))"
            );
            
            // Create LOCATION table
            stmt.executeUpdate(
                "CREATE TABLE LOCATION (" +
                "lId VARCHAR(12) NOT NULL, " +
                "size INT NOT NULL, " +
                "type VARCHAR(8) NOT NULL, " +
                "PRIMARY KEY (lId))"
            );
            
            // Create EXITSTO table
            stmt.executeUpdate(
                "CREATE TABLE EXITSTO (" +
                "enterId VARCHAR(12) NOT NULL, " +
                "exitId VARCHAR(12) NOT NULL, " +
                "FOREIGN KEY (enterId) REFERENCES LOCATION (lId), " +
                "FOREIGN KEY (exitId) REFERENCES LOCATION (lId))"
            );
            
            // Create GAMECHARACTER table
            stmt.executeUpdate(
                "CREATE TABLE GAMECHARACTER (" +
                "name VARCHAR(20) NOT NULL, " +
                "playerId VARCHAR(12) NOT NULL, " +
                "maxPoints INT NOT NULL, " +
                "currentPoints INT NOT NULL, " +
                "stamina INT NOT NULL, " +
                "strength INT NOT NULL, " +
                "profilePicture MEDIUMBLOB, " +
                "locationId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (name), " +
                "FOREIGN KEY (playerId) REFERENCES PLAYER (loginId), " +
                "FOREIGN KEY (locationId) REFERENCES LOCATION (lId))"
            );
            
            // Create ITEM table
            stmt.executeUpdate(
                "CREATE TABLE ITEM (" +
                "IdItem VARCHAR(12) NOT NULL, " +
                "description TEXT, " +
                "volume INT NOT NULL, " +
                "weight DECIMAL(4, 2) NOT NULL, " +
                "isTwoHanded BOOLEAN NOT NULL, " +
                "ownedBy VARCHAR(20), " +
                "PRIMARY KEY (IdItem), " +
                "FOREIGN KEY (ownedBy) REFERENCES GAMECHARACTER (name))"
            );
            
            // Create ARMOR table
            stmt.executeUpdate(
                "CREATE TABLE ARMOR (" +
                "aId VARCHAR(12) NOT NULL, " +
                "protection INT NOT NULL, " +
                "equipLocation VARCHAR(12) NOT NULL, " +
                "isEquiped BOOLEAN NOT NULL, " +
                "PRIMARY KEY (aId), " +
                "FOREIGN KEY (aId) REFERENCES ITEM (IdItem))"
            );
            
            // Create CONTAINER table
            stmt.executeUpdate(
                "CREATE TABLE CONTAINER (" +
                "cId VARCHAR(12) NOT NULL, " +
                "volume INT NOT NULL, " +
                "weight DECIMAL(4,2) NOT NULL, " +
                "PRIMARY KEY (cId), " +
                "FOREIGN KEY (cId) REFERENCES ITEM (IdItem))"
            );
            
            // Create CONTAINEDIN table
            stmt.executeUpdate(
                "CREATE TABLE CONTAINEDIN (" +
                "cId VARCHAR(12) NOT NULL, " +
                "IdItem VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (cId), " +
                "FOREIGN KEY (cId) REFERENCES CONTAINER (cId), " +
                "FOREIGN KEY (IdItem) REFERENCES ITEM (IdItem))"
            );
            
            // Create ABILITY table
            stmt.executeUpdate(
                "CREATE TABLE ABILITY (" +
                "name VARCHAR(12) NOT NULL, " +
                "targetStat VARCHAR(20) NOT NULL, " +
                "amount INT NOT NULL, " +
                "durationToExecute INT NOT NULL, " +
                "cooldown DECIMAL(3,1) NOT NULL, " +
                "uses INT NOT NULL, " +
                "PRIMARY KEY (name))"
            );
            
            // Create WEAPON table
            stmt.executeUpdate(
                "CREATE TABLE WEAPON (" +
                "wId VARCHAR(12) NOT NULL, " +
                "ability VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (wId), " +
                "FOREIGN KEY (wId) REFERENCES ITEM (IdItem), " +
                "FOREIGN KEY (ability) REFERENCES ABILITY (name))"
            );
            
            // Create CREATURE table
            stmt.executeUpdate(
                "CREATE TABLE CREATURE (" +
                "name VARCHAR(12) NOT NULL, " +
                "damageProtection INT NOT NULL, " +
                "currentPoints INT NOT NULL, " +
                "stamina INT NOT NULL, " +
                "maxPoints INT NOT NULL, " +
                "locationId VARCHAR(12) NOT NULL, " +
                "ability VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (name), " +
                "FOREIGN KEY (locationId) REFERENCES LOCATION (lId), " +
                "FOREIGN KEY (ability) REFERENCES ABILITY (name))"
            );
            
            // Create CREATURELIKES table
            stmt.executeUpdate(
                "CREATE TABLE CREATURELIKES (" +
                "cId VARCHAR(12) NOT NULL, " +
                "rId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (cId), " +
                "FOREIGN KEY (cId) REFERENCES CREATURE (name), " +
                "FOREIGN KEY (rId) REFERENCES CREATURE (name))"
            );
            
            // Create CREATUREHATES table
            stmt.executeUpdate(
                "CREATE TABLE CREATUREHATES (" +
                "cId VARCHAR(12) NOT NULL, " +
                "rId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (cId), " +
                "FOREIGN KEY (cId) REFERENCES CREATURE (name), " +
                "FOREIGN KEY (rId) REFERENCES CREATURE (name))"
            );
            
            // Create PLAYERLIKES table
            stmt.executeUpdate(
                "CREATE TABLE PLAYERLIKES (" +
                "pId VARCHAR(12) NOT NULL, " +
                "rId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (pId), " +
                "FOREIGN KEY (pId) REFERENCES PLAYER (loginId), " +
                "FOREIGN KEY (rId) REFERENCES CREATURE (name))"
            );
            
            // Create PLAYERHATES table
            stmt.executeUpdate(
                "CREATE TABLE PLAYERHATES (" +
                "pId VARCHAR(12) NOT NULL, " +
                "rId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (pId), " +
                "FOREIGN KEY (pId) REFERENCES PLAYER (loginId), " +
                "FOREIGN KEY (rId) REFERENCES CREATURE (name))"
            );
            
            // Create ALLOWEDTOGO table
            stmt.executeUpdate(
                "CREATE TABLE ALLOWEDTOGO (" +
                "cId VARCHAR(12) NOT NULL, " +
                "lId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (cId), " +
                "FOREIGN KEY (cId) REFERENCES CREATURE (name), " +
                "FOREIGN KEY (lId) REFERENCES LOCATION (lId))"
            );
            
            // Create ITEMSPRESENT table
            stmt.executeUpdate(
                "CREATE TABLE ITEMSPRESENT (" +
                "lId VARCHAR(12) NOT NULL, " +
                "ItemId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (lId), " +
                "FOREIGN KEY (lId) REFERENCES LOCATION (lId), " +
                "FOREIGN KEY (ItemId) REFERENCES ITEM (IdItem))"
            );
            
            // Create CHARACTERSPRESENT table
            stmt.executeUpdate(
                "CREATE TABLE CHARACTERSPRESENT (" +
                "lId VARCHAR(12) NOT NULL, " +
                "cId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (lId), " +
                "FOREIGN KEY (lId) REFERENCES LOCATION (lId), " +
                "FOREIGN KEY (cId) REFERENCES GAMECHARACTER (name))"
            );
            
            // Create CREATURESPRESENT table
            stmt.executeUpdate(
                "CREATE TABLE CREATURESPRESENT (" +
                "lId VARCHAR(12) NOT NULL, " +
                "creatureId VARCHAR(12) NOT NULL, " +
                "PRIMARY KEY (lId), " +
                "FOREIGN KEY (lId) REFERENCES LOCATION (lId), " +
                "FOREIGN KEY (creatureId) REFERENCES CREATURE (name))"
            );
            
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
            // Insert sample persons
            String insertPersonSQL = "INSERT INTO PERSON (loginId, email, password, dateCreated) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertPersonSQL);
            
            // Insert user1
            pstmt.setString(1, "user1");
            pstmt.setString(2, "user1@example.com");
            pstmt.setString(3, "password123"); // In a real app, this would be hashed
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if already exists
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
            // Insert user2
            pstmt.setString(1, "user2");
            pstmt.setString(2, "user2@example.com");
            pstmt.setString(3, "password456");
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if already exists
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
            // Insert players
            String insertPlayerSQL = "INSERT INTO PLAYER (loginId, isSilenced, isBlocked, watchedBy) VALUES (?, ?, ?, ?)";
            pstmt = connection.prepareStatement(insertPlayerSQL);
            
            // Insert user1 as player
            pstmt.setString(1, "user1");
            pstmt.setBoolean(2, false);
            pstmt.setBoolean(3, false);
            pstmt.setString(4, null);
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if already exists
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
            // Insert user2 as player
            pstmt.setString(1, "user2");
            pstmt.setBoolean(2, false);
            pstmt.setBoolean(3, false);
            pstmt.setString(4, null);
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if already exists
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
            // Insert a sample location
            String insertLocationSQL = "INSERT INTO LOCATION (lId, size, type) VALUES (?, ?, ?)";
            pstmt = connection.prepareStatement(insertLocationSQL);
            
            // Insert default location
            pstmt.setString(1, "default");
            pstmt.setInt(2, 100);
            pstmt.setString(3, "forest");
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if already exists
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
            // Insert a sample game character
            String insertCharacterSQL = "INSERT INTO GAMECHARACTER " +
                "(name, playerId, maxPoints, currentPoints, stamina, strength, locationId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(insertCharacterSQL);
            
            // Insert a sample character
            pstmt.setString(1, "Warrior");
            pstmt.setString(2, "user1");
            pstmt.setInt(3, 150);
            pstmt.setInt(4, 150);
            pstmt.setInt(5, 25);
            pstmt.setInt(6, 30);
            pstmt.setString(7, "default");
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if already exists
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
            
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