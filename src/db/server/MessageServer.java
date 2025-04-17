/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Message Server - Handles client requests and database operations
 * 
 * @author [YOUR NAME HERE]
 */
package db.server;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.StringTokenizer;

public class MessageServer {
    // private static final String DB_URL = "jdbc:mysql://db.engr.ship.edu:3306/cmsc471_27?useTimezone=true&serverTimezone=UTC";
    // private static final String DB_USER = "cmsc471_27";
    // private static final String DB_PASSWORD = "Password_27";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Password_27";
    private static Connection dbConn;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            // Setup database connection
            setupDatabaseConnection();
            
            // Create tables and populate with initial data
            createTables();
            populateTables();
            
            // Create stored procedure if it doesn't exist
            createStoredProcedure();
            
            // Start server
            serverSocket = new ServerSocket(4446);
            System.out.println("Message server started on port 4446");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupDatabaseConnection() throws SQLException {
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        dbConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Creates all required database tables.
     * Part I of the assignment.
     */
    private static void createTables() {
        if (dbConn == null) {
            System.err.println("No database connection");
            return;
        }
        
        try {
            Statement stmt = dbConn.createStatement();
            
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
    private static void populateTables() {
        if (dbConn == null) {
            System.err.println("No database connection");
            return;
        }
        
        try {
            // Insert sample persons
            String insertPersonSQL = "INSERT INTO PERSON (loginId, email, password, dateCreated) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = dbConn.prepareStatement(insertPersonSQL);
            
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
            pstmt = dbConn.prepareStatement(insertPlayerSQL);
            
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
            pstmt = dbConn.prepareStatement(insertLocationSQL);
            
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
            pstmt = dbConn.prepareStatement(insertCharacterSQL);
            
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

    private static void createStoredProcedure() throws SQLException {
        // Create stored procedure for character creation
        String createProcSQL = "CREATE PROCEDURE IF NOT EXISTS create_character(" +
            "IN p_name VARCHAR(20), " +
            "IN p_playerId VARCHAR(12), " +
            "IN p_maxPoints INT, " +
            "IN p_currentPoints INT, " +
            "IN p_stamina INT, " +
            "IN p_strength INT, " +
            "IN p_locationId VARCHAR(12)) " +
            "BEGIN " +
            "INSERT INTO GAMECHARACTER (name, playerId, maxPoints, currentPoints, stamina, strength, locationId) " +
            "VALUES (p_name, p_playerId, p_maxPoints, p_currentPoints, p_stamina, p_strength, p_locationId); " +
            "END";
        
        try (Statement stmt = dbConn.createStatement()) {
            stmt.execute(createProcSQL);
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    String response = processMessage(message);
                    out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String processMessage(String message) {
            try {
                StringTokenizer tokenizer = new StringTokenizer(message, ":");
                String command = tokenizer.nextToken();

                switch (command) {
                    case "INSERT":
                        return handleInsert(tokenizer);
                    case "SELECT":
                        return handleSelect(tokenizer);
                    case "UPDATE":
                        return handleUpdate(tokenizer);
                    case "DELETE":
                        return handleDelete(tokenizer);
                    case "PROCEDURE":
                        return handleProcedure(tokenizer);
                    default:
                        return "ERROR: Unknown command";
                }
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }

        private String handleInsert(StringTokenizer tokenizer) throws SQLException {
            String table = tokenizer.nextToken();
            String columns = tokenizer.nextToken();
            String values = tokenizer.nextToken();

            String[] columnArray = columns.split(",");
            String[] valueArray = values.split(",");

            StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
            sql.append(String.join(",", columnArray));
            sql.append(") VALUES (");
            sql.append("?,".repeat(columnArray.length - 1));
            sql.append("?)");

            try (PreparedStatement pstmt = dbConn.prepareStatement(sql.toString())) {
                for (int i = 0; i < valueArray.length; i++) {
                    pstmt.setString(i + 1, valueArray[i]);
                }
                pstmt.executeUpdate();
                return "SUCCESS: Record inserted";
            }
        }

        private String handleSelect(StringTokenizer tokenizer) throws SQLException {
            String table = tokenizer.nextToken();
            String columns = tokenizer.nextToken();
            String operator = tokenizer.nextToken();
            String value = tokenizer.nextToken();

            String sql = "SELECT * FROM " + table + " WHERE " + columns + " " + operator + " ?";
            
            try (PreparedStatement pstmt = dbConn.prepareStatement(sql)) {
                pstmt.setString(1, value);
                ResultSet rs = pstmt.executeQuery();
                
                StringBuilder result = new StringBuilder();
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                
                // Add column headers
                for (int i = 1; i <= cols; i++) {
                    result.append(meta.getColumnName(i)).append("\t");
                }
                result.append("\n");
                
                // Add data rows
                while (rs.next()) {
                    for (int i = 1; i <= cols; i++) {
                        result.append(rs.getString(i)).append("\t");
                    }
                    result.append("\n");
                }
                
                return result.toString();
            }
        }

        private String handleUpdate(StringTokenizer tokenizer) throws SQLException {
            String table = tokenizer.nextToken();
            String column = tokenizer.nextToken();
            String newValue = tokenizer.nextToken();
            String whereColumn = tokenizer.nextToken();
            String operator = tokenizer.nextToken();
            String whereValue = tokenizer.nextToken();

            String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + whereColumn + " " + operator + " ?";
            
            try (PreparedStatement pstmt = dbConn.prepareStatement(sql)) {
                pstmt.setString(1, newValue);
                pstmt.setString(2, whereValue);
                pstmt.executeUpdate();
                return "SUCCESS: Record updated";
            }
        }

        private String handleDelete(StringTokenizer tokenizer) throws SQLException {
            String table = tokenizer.nextToken();
            String column = tokenizer.nextToken();
            String operator = tokenizer.nextToken();
            String value = tokenizer.nextToken();

            String sql = "DELETE FROM " + table + " WHERE " + column + " " + operator + " ?";
            
            try (PreparedStatement pstmt = dbConn.prepareStatement(sql)) {
                pstmt.setString(1, value);
                pstmt.executeUpdate();
                return "SUCCESS: Record deleted";
            }
        }

        private String handleProcedure(StringTokenizer tokenizer) throws SQLException {
            String procedure = tokenizer.nextToken();
            String params = tokenizer.nextToken();
            String[] paramArray = params.split(",");

            if (procedure.equals("create_character")) {
                try (CallableStatement cstmt = dbConn.prepareCall("{call create_character(?, ?, ?, ?, ?, ?, ?)}")) {
                    cstmt.setString(1, paramArray[0]); // name
                    cstmt.setString(2, paramArray[1]); // playerId
                    cstmt.setInt(3, Integer.parseInt(paramArray[2])); // maxPoints
                    cstmt.setInt(4, Integer.parseInt(paramArray[3])); // currentPoints
                    cstmt.setInt(5, Integer.parseInt(paramArray[4])); // stamina
                    cstmt.setInt(6, Integer.parseInt(paramArray[5])); // strength
                    cstmt.setString(7, paramArray[6]); // locationId
                    cstmt.execute();
                    return "SUCCESS: Character created using stored procedure";
                }
            }
            return "ERROR: Unknown procedure";
        }
    }
} 