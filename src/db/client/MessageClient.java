/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Message Client - Handles communication with the message server
 * 
 * @author [YOUR NAME HERE]
 */
package db.client;

import java.net.*;
import java.io.*;

public class MessageClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4446;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public MessageClient() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    public String sendMessage(String message) {
        try {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // Helper methods for common operations
    public String insert(String table, String columns, String values) {
        return sendMessage("INSERT:" + table + ":" + columns + ":" + values);
    }

    public String select(String table, String columns, String operator, String value) {
        return sendMessage("SELECT:" + table + ":" + columns + ":" + operator + ":" + value);
    }

    public String update(String table, String column, String newValue, String whereColumn, String operator, String whereValue) {
        return sendMessage("UPDATE:" + table + ":" + column + ":" + newValue + ":" + whereColumn + ":" + operator + ":" + whereValue);
    }

    public String delete(String table, String column, String operator, String value) {
        return sendMessage("DELETE:" + table + ":" + column + ":" + operator + ":" + value);
    }

    public String callProcedure(String procedure, String params) {
        return sendMessage("PROCEDURE:" + procedure + ":" + params);
    }
} 