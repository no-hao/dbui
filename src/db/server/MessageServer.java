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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Password_27";
    private static Connection dbConn;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            // Setup database connection
            setupDatabaseConnection();
            
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