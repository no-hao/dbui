/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Message Server - Handles communication between UI and database
 * 
 * @author [YOUR NAME HERE]
 */
package db.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Message server that acts as an intermediary between the UI and database.
 * This is used for Part III of the assignment.
 */
public class MessageServer {
    private static final int PORT = 8888;
    // School server connection (commented out)
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
    // private static final String DB_USER = "your_db_username"; // Replace with actual username
    // private static final String DB_PASSWORD = "your_db_password"; // Replace with actual password
    
    // Local MySQL connection
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "Password_27"; 
    
    private ServerSocket serverSocket;
    private Connection dbConnection;
    private boolean running;
    
    // Cache of prepared statements for better performance
    private Map<String, PreparedStatement> preparedStatements = new HashMap<>();
    
    /**
     * Starts the message server.
     */
    public void start() {
        try {
            // Connect to the database
            connectToDatabase();
            
            // Initialize stored procedures
            initializeStoredProcedures();
            
            // Start the server
            serverSocket = new ServerSocket(PORT);
            running = true;
            
            System.out.println("Message Server started on port " + PORT);
            
            // Accept client connections
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    
                    // Handle client in a new thread
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Stops the message server.
     */
    public void stop() {
        running = false;
        
        // Close the server socket
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
        
        // Close prepared statements
        for (PreparedStatement stmt : preparedStatements.values()) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing prepared statement: " + e.getMessage());
            }
        }
        
        // Close the database connection
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
        
        System.out.println("Message Server stopped");
    }
    
    /**
     * Connects to the database.
     * 
     * @throws SQLException if a database access error occurs
     */
    private void connectToDatabase() throws SQLException {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + e.getMessage());
        }
    }
    
    /**
     * Initializes stored procedures in the database.
     * 
     * @throws SQLException if a database access error occurs
     */
    private void initializeStoredProcedures() throws SQLException {
        try (Statement stmt = dbConnection.createStatement()) {
            // Example stored procedure for getting character details
            String createProcedure = 
                "CREATE PROCEDURE IF NOT EXISTS GetCharacterDetails(IN char_name VARCHAR(20)) " +
                "BEGIN " +
                "    SELECT * FROM GAMECHARACTER WHERE name = char_name; " +
                "END";
            
            stmt.execute(createProcedure);
            System.out.println("Stored procedures initialized");
        }
    }
    
    /**
     * Handles a client connection.
     * 
     * @param clientSocket the client socket
     */
    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                
                // Parse the message
                String response = processMessage(message);
                
                // Send response back to client
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Processes a message from the client and returns a response.
     * 
     * @param message the message from the client
     * @return the response to send back to the client
     */
    private String processMessage(String message) {
        // Parse the message (format: COMMAND:TABLE:PARAMS)
        String[] parts = message.split(":");
        if (parts.length < 2) {
            return "ERROR:Invalid message format";
        }
        
        String command = parts[0];
        String table = parts[1];
        
        try {
            switch (command) {
                case "SELECT":
                    return handleSelect(parts);
                case "INSERT":
                    return handleInsert(parts);
                case "UPDATE":
                    return handleUpdate(parts);
                case "DELETE":
                    return handleDelete(parts);
                case "PROCEDURE":
                    return handleProcedure(parts);
                default:
                    return "ERROR:Unknown command";
            }
        } catch (SQLException e) {
            return "ERROR:" + e.getMessage();
        }
    }
    
    /**
     * Handles a SELECT command.
     * 
     * @param parts the parsed message parts
     * @return the query result
     * @throws SQLException if a database access error occurs
     */
    private String handleSelect(String[] parts) throws SQLException {
        if (parts.length < 4) {
            return "ERROR:Invalid SELECT format";
        }
        
        String table = parts[1];
        String whereCol = parts[2];
        String operator = parts[3];
        String value = parts.length > 4 ? parts[4] : "";
        
        // Create SQL statement with PreparedStatement
        String sql = "SELECT * FROM " + table;
        if (!whereCol.equals("*")) {
            sql += " WHERE " + whereCol + " " + operator + " ?";
        }
        
        PreparedStatement pstmt = getOrCreatePreparedStatement(sql);
        if (!whereCol.equals("*")) {
            pstmt.setString(1, value);
        }
        
        ResultSet rs = pstmt.executeQuery();
        
        // Process results into a string
        StringBuilder result = new StringBuilder("RESULTS:");
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // Add column headers
        for (int i = 1; i <= columnCount; i++) {
            result.append(metaData.getColumnName(i)).append(",");
        }
        result.deleteCharAt(result.length() - 1); // Remove last comma
        result.append(";");
        
        // Add data rows
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(rs.getString(i)).append(",");
            }
            result.deleteCharAt(result.length() - 1); // Remove last comma
            result.append(";");
        }
        
        return result.toString();
    }
    
    /**
     * Handles an INSERT command.
     * 
     * @param parts the parsed message parts
     * @return the result of the operation
     * @throws SQLException if a database access error occurs
     */
    private String handleInsert(String[] parts) throws SQLException {
        if (parts.length < 4) {
            return "ERROR:Invalid INSERT format";
        }
        
        String table = parts[1];
        String[] columns = parts[2].split(",");
        String[] values = parts[3].split(",");
        
        if (columns.length != values.length) {
            return "ERROR:Columns and values count mismatch";
        }
        
        // Create SQL statement
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
        for (String column : columns) {
            sql.append(column).append(",");
        }
        sql.deleteCharAt(sql.length() - 1); // Remove last comma
        
        sql.append(") VALUES (");
        for (int i = 0; i < values.length; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1); // Remove last comma
        sql.append(")");
        
        PreparedStatement pstmt = getOrCreatePreparedStatement(sql.toString());
        
        // Set parameter values
        for (int i = 0; i < values.length; i++) {
            pstmt.setString(i + 1, values[i]);
        }
        
        int rowsAffected = pstmt.executeUpdate();
        return "SUCCESS:Rows affected: " + rowsAffected;
    }
    
    /**
     * Handles an UPDATE command.
     * 
     * @param parts the parsed message parts
     * @return the result of the operation
     * @throws SQLException if a database access error occurs
     */
    private String handleUpdate(String[] parts) throws SQLException {
        if (parts.length < 6) {
            return "ERROR:Invalid UPDATE format";
        }
        
        String table = parts[1];
        String setColumn = parts[2];
        String setValue = parts[3];
        String whereColumn = parts[4];
        String whereValue = parts[5];
        
        // Create SQL statement
        String sql = "UPDATE " + table + " SET " + setColumn + " = ? WHERE " + whereColumn + " = ?";
        
        PreparedStatement pstmt = getOrCreatePreparedStatement(sql);
        pstmt.setString(1, setValue);
        pstmt.setString(2, whereValue);
        
        int rowsAffected = pstmt.executeUpdate();
        return "SUCCESS:Rows affected: " + rowsAffected;
    }
    
    /**
     * Handles a DELETE command.
     * 
     * @param parts the parsed message parts
     * @return the result of the operation
     * @throws SQLException if a database access error occurs
     */
    private String handleDelete(String[] parts) throws SQLException {
        if (parts.length < 4) {
            return "ERROR:Invalid DELETE format";
        }
        
        String table = parts[1];
        String whereColumn = parts[2];
        String whereValue = parts[3];
        
        // Create SQL statement
        String sql = "DELETE FROM " + table + " WHERE " + whereColumn + " = ?";
        
        PreparedStatement pstmt = getOrCreatePreparedStatement(sql);
        pstmt.setString(1, whereValue);
        
        int rowsAffected = pstmt.executeUpdate();
        return "SUCCESS:Rows affected: " + rowsAffected;
    }
    
    /**
     * Handles a PROCEDURE command.
     * 
     * @param parts the parsed message parts
     * @return the result of the operation
     * @throws SQLException if a database access error occurs
     */
    private String handleProcedure(String[] parts) throws SQLException {
        if (parts.length < 3) {
            return "ERROR:Invalid PROCEDURE format";
        }
        
        String procedureName = parts[1];
        String[] params = new String[parts.length - 2];
        System.arraycopy(parts, 2, params, 0, params.length);
        
        // Create call statement
        StringBuilder callStmt = new StringBuilder("CALL " + procedureName + "(");
        for (int i = 0; i < params.length; i++) {
            callStmt.append("?,");
        }
        if (params.length > 0) {
            callStmt.deleteCharAt(callStmt.length() - 1); // Remove last comma
        }
        callStmt.append(")");
        
        CallableStatement cstmt = dbConnection.prepareCall(callStmt.toString());
        
        // Set parameter values
        for (int i = 0; i < params.length; i++) {
            cstmt.setString(i + 1, params[i]);
        }
        
        boolean hasResults = cstmt.execute();
        
        if (hasResults) {
            ResultSet rs = cstmt.getResultSet();
            
            // Process results into a string
            StringBuilder result = new StringBuilder("RESULTS:");
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Add column headers
            for (int i = 1; i <= columnCount; i++) {
                result.append(metaData.getColumnName(i)).append(",");
            }
            result.deleteCharAt(result.length() - 1); // Remove last comma
            result.append(";");
            
            // Add data rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getString(i)).append(",");
                }
                result.deleteCharAt(result.length() - 1); // Remove last comma
                result.append(";");
            }
            
            return result.toString();
        } else {
            int updateCount = cstmt.getUpdateCount();
            return "SUCCESS:Rows affected: " + updateCount;
        }
    }
    
    /**
     * Gets an existing prepared statement or creates a new one if it doesn't exist.
     * 
     * @param sql the SQL statement
     * @return the prepared statement
     * @throws SQLException if a database access error occurs
     */
    private PreparedStatement getOrCreatePreparedStatement(String sql) throws SQLException {
        PreparedStatement pstmt = preparedStatements.get(sql);
        if (pstmt == null) {
            pstmt = dbConnection.prepareStatement(sql);
            preparedStatements.put(sql, pstmt);
        }
        return pstmt;
    }
    
    /**
     * Main method to start the message server.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        MessageServer server = new MessageServer();
        server.start();
    }
} 