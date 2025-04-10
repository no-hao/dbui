/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Message Client - Client for communicating with the message server
 * 
 * @author [YOUR NAME HERE]
 */
package db.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Client for communicating with the message server.
 * This is used by the UI to send requests to the database.
 */
public class MessageClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    /**
     * Connects to the message server.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Disconnects from the message server.
     */
    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting from server: " + e.getMessage());
        }
    }
    
    /**
     * Sends a message to the server and returns the response.
     * 
     * @param message the message to send
     * @return the server's response
     * @throws IOException if an I/O error occurs
     */
    private String sendMessage(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }
    
    /**
     * Executes a SELECT query.
     * 
     * @param table the table to query
     * @param whereColumn the column to filter on (use "*" for no filter)
     * @param operator the comparison operator (e.g., "=", ">", "<")
     * @param value the value to compare against
     * @return a list of maps representing the query results
     */
    public List<List<String>> select(String table, String whereColumn, String operator, String value) {
        try {
            String message = String.format("SELECT:%s:%s:%s:%s", table, whereColumn, operator, value);
            String response = sendMessage(message);
            
            return parseResults(response);
        } catch (IOException e) {
            System.err.println("Error executing SELECT: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Executes an INSERT query.
     * 
     * @param table the table to insert into
     * @param columns the columns to insert values for
     * @param values the values to insert
     * @return the number of rows affected
     */
    public int insert(String table, String[] columns, String[] values) {
        try {
            StringBuilder message = new StringBuilder("INSERT:");
            message.append(table).append(":");
            
            // Add columns
            for (int i = 0; i < columns.length; i++) {
                message.append(columns[i]);
                if (i < columns.length - 1) {
                    message.append(",");
                }
            }
            message.append(":");
            
            // Add values
            for (int i = 0; i < values.length; i++) {
                message.append(values[i]);
                if (i < values.length - 1) {
                    message.append(",");
                }
            }
            
            String response = sendMessage(message.toString());
            
            if (response.startsWith("SUCCESS:")) {
                String[] parts = response.split(":");
                return Integer.parseInt(parts[1].split(" ")[2]);
            }
            
            return 0;
        } catch (IOException e) {
            System.err.println("Error executing INSERT: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Executes an UPDATE query.
     * 
     * @param table the table to update
     * @param setColumn the column to update
     * @param setValue the new value
     * @param whereColumn the column to filter on
     * @param whereValue the value to compare against
     * @return the number of rows affected
     */
    public int update(String table, String setColumn, String setValue, String whereColumn, String whereValue) {
        try {
            String message = String.format("UPDATE:%s:%s:%s:%s:%s", 
                    table, setColumn, setValue, whereColumn, whereValue);
            String response = sendMessage(message);
            
            if (response.startsWith("SUCCESS:")) {
                String[] parts = response.split(":");
                return Integer.parseInt(parts[1].split(" ")[2]);
            }
            
            return 0;
        } catch (IOException e) {
            System.err.println("Error executing UPDATE: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Executes a DELETE query.
     * 
     * @param table the table to delete from
     * @param whereColumn the column to filter on
     * @param whereValue the value to compare against
     * @return the number of rows affected
     */
    public int delete(String table, String whereColumn, String whereValue) {
        try {
            String message = String.format("DELETE:%s:%s:%s", table, whereColumn, whereValue);
            String response = sendMessage(message);
            
            if (response.startsWith("SUCCESS:")) {
                String[] parts = response.split(":");
                return Integer.parseInt(parts[1].split(" ")[2]);
            }
            
            return 0;
        } catch (IOException e) {
            System.err.println("Error executing DELETE: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Calls a stored procedure.
     * 
     * @param procedureName the name of the procedure
     * @param params the parameters to pass to the procedure
     * @return a list of maps representing the query results
     */
    public List<List<String>> callProcedure(String procedureName, String... params) {
        try {
            StringBuilder message = new StringBuilder("PROCEDURE:");
            message.append(procedureName);
            
            for (String param : params) {
                message.append(":").append(param);
            }
            
            String response = sendMessage(message.toString());
            
            return parseResults(response);
        } catch (IOException e) {
            System.err.println("Error calling procedure: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Parses the results from a server response.
     * 
     * @param response the server response
     * @return a list of maps representing the query results
     */
    private List<List<String>> parseResults(String response) {
        List<List<String>> results = new ArrayList<>();
        
        if (response.startsWith("RESULTS:")) {
            String[] sections = response.substring(8).split(";");
            
            if (sections.length > 0) {
                String[] columns = sections[0].split(",");
                
                // Add column headers as first row
                List<String> headerRow = new ArrayList<>();
                for (String column : columns) {
                    headerRow.add(column);
                }
                results.add(headerRow);
                
                // Add data rows
                for (int i = 1; i < sections.length; i++) {
                    if (!sections[i].isEmpty()) {
                        String[] values = sections[i].split(",");
                        
                        List<String> dataRow = new ArrayList<>();
                        for (String value : values) {
                            dataRow.add(value);
                        }
                        results.add(dataRow);
                    }
                }
            }
        }
        
        return results;
    }
    
    /**
     * Main method for testing the message client.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        MessageClient client = new MessageClient();
        
        if (client.connect()) {
            System.out.println("Connected to server");
            
            // Test SELECT
            List<List<String>> results = client.select("PLAYER", "*", "=", "");
            System.out.println("SELECT results: " + results);
            
            // Test INSERT
            int rowsAffected = client.insert("PLAYER", 
                    new String[]{"loginId", "isSilenced", "isBlocked"}, 
                    new String[]{"test_user", "0", "0"});
            System.out.println("INSERT affected " + rowsAffected + " rows");
            
            // Test procedure call
            results = client.callProcedure("GetCharacterDetails", "Archer");
            System.out.println("Procedure results: " + results);
            
            client.disconnect();
            System.out.println("Disconnected from server");
        } else {
            System.out.println("Failed to connect to server");
        }
    }
} 