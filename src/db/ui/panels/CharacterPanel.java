/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Character Panel - Displays and manages game characters
 * 
 * @author [YOUR NAME HERE]
 */
package db.ui.panels;

import db.ui.dialogs.CharacterFormDialog;
import db.Character;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for managing characters.
 * Provides functionality to view, add, edit, and delete characters.
 */
public class CharacterPanel extends JPanel {
    private JTable characterTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;

    // Database-backed list of characters
    private List<Character> characters;

    public CharacterPanel() {
        setLayout(new BorderLayout());

        // Initialize character list from database
        characters = new ArrayList<>();
        loadCharactersFromDatabase();

        // Create table model and populate with data
        String[] columns = {"Name", "Player ID", "Max HP", "Strength", "Stamina"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing in the table
            }
        };
        populateTable();

        // Create table and wrap in a scroll pane
        characterTable = new JTable(tableModel);
        add(new JScrollPane(characterTable), BorderLayout.CENTER);

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add Character");
        btnEdit = new JButton("Edit Selected");
        btnDelete = new JButton("Delete Selected");

        // Add action listeners for buttons
        btnAdd.addActionListener(e -> showCharacterForm(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = characterTable.getSelectedRow();
            if (selectedRow != -1) {
                String name = (String) tableModel.getValueAt(selectedRow, 0);
                showCharacterForm(getCharacterByName(name));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a character to edit.");
            }
        });
        btnDelete.addActionListener(e -> deleteSelectedCharacter());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads characters from the database
     */
    private void loadCharactersFromDatabase() {
        characters.clear();
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
            
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT name, playerId, locationId, maxPoints, currentPoints, stamina, strength " +
                "FROM GAMECHARACTER");
            
            while (rs.next()) {
                String name = rs.getString("name");
                String playerId = rs.getString("playerId");
                String locationId = rs.getString("locationId");
                int maxPoints = rs.getInt("maxPoints");
                int currentPoints = rs.getInt("currentPoints");
                int stamina = rs.getInt("stamina");
                int strength = rs.getInt("strength");
                
                Character character = new Character(
                    name, playerId, maxPoints, currentPoints, stamina, strength);
                character.setLocationId(locationId);
                
                characters.add(character);
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("Loaded " + characters.size() + " characters from database");
        } catch (Exception e) {
            System.out.println("Error loading characters from database: " + e.getMessage());
            // Add fallback mock data if DB connection fails
            if (characters.isEmpty()) {
                characters.add(new Character(1, "Archer", "user1@example.com", 100, 20, 15));
                characters.add(new Character(2, "Mage", "user2@example.com", 80, 10, 25));
            }
        }
    }

    /**
     * Populates the table with character data.
     */
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Character character : characters) {
            tableModel.addRow(new Object[]{
                character.getName(),
                character.getPlayerId(),
                character.getMaxHp(),
                character.getStrength(),
                character.getStamina()
            });
        }
    }

    /**
     * Shows a form dialog for adding or editing a character.
     * @param character The character to edit (null if adding a new one).
     */
    private void showCharacterForm(Character character) {
        CharacterFormDialog dialog = new CharacterFormDialog(character);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Character updatedCharacter = dialog.getCharacter();
            
            try {
                java.sql.Connection conn = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                
                if (character == null) {
                    // First, check if the default location exists
                    boolean defaultLocationExists = false;
                    java.sql.PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM LOCATION WHERE lId = ?");
                    checkStmt.setString(1, "default");
                    java.sql.ResultSet checkRs = checkStmt.executeQuery();
                    
                    if (checkRs.next() && checkRs.getInt(1) > 0) {
                        defaultLocationExists = true;
                    }
                    
                    checkRs.close();
                    checkStmt.close();
                    
                    // If default location doesn't exist, create it
                    if (!defaultLocationExists) {
                        java.sql.PreparedStatement createLocStmt = conn.prepareStatement(
                            "INSERT INTO LOCATION (lId, size, type) VALUES (?, ?, ?)");
                        createLocStmt.setString(1, "default");
                        createLocStmt.setInt(2, 100);
                        createLocStmt.setString(3, "forest");
                        createLocStmt.executeUpdate();
                        createLocStmt.close();
                        System.out.println("Created default location");
                    }
                    
                    // Add new character to database
                    java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO GAMECHARACTER (name, playerId, locationId, maxPoints, currentPoints, stamina, strength) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    stmt.setString(1, updatedCharacter.getName());
                    stmt.setString(2, updatedCharacter.getPlayerId());
                    stmt.setString(3, "default");  // default location
                    stmt.setInt(4, updatedCharacter.getMaxPoints());
                    stmt.setInt(5, updatedCharacter.getMaxPoints());  // start with full points
                    stmt.setInt(6, updatedCharacter.getStamina());
                    stmt.setInt(7, updatedCharacter.getStrength());
                    stmt.executeUpdate();
                    
                    // Add to local list
                    characters.add(updatedCharacter);
                    System.out.println("Character added to database: " + updatedCharacter.getName());
                    
                    stmt.close();
                } else {
                    // Update existing character in database
                    java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE GAMECHARACTER SET playerId = ?, maxPoints = ?, currentPoints = ?, " +
                        "stamina = ?, strength = ? WHERE name = ?");
                    stmt.setString(1, updatedCharacter.getPlayerId());
                    stmt.setInt(2, updatedCharacter.getMaxPoints());
                    stmt.setInt(3, updatedCharacter.getCurrentPoints());
                    stmt.setInt(4, updatedCharacter.getStamina());
                    stmt.setInt(5, updatedCharacter.getStrength());
                    stmt.setString(6, updatedCharacter.getName());
                    stmt.executeUpdate();
                    
                    // Update in local list
                    int index = characters.indexOf(character);
                    if (index >= 0) {
                        characters.set(index, updatedCharacter);
                    }
                    
                    stmt.close();
                    System.out.println("Character updated in database: " + updatedCharacter.getName());
                }
                
                conn.close();
            } catch (Exception e) {
                System.out.println("Error saving character to database: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Error saving to database: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            // Reload from database to refresh data
            loadCharactersFromDatabase();
            populateTable(); // Refresh table
        }
    }

    /**
     * Deletes the selected character from the database and list.
     */
    private void deleteSelectedCharacter() {
        int selectedRow = characterTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this character?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    java.sql.Connection conn = java.sql.DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                    
                    // Delete character from database
                    java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM GAMECHARACTER WHERE name = ?");
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                    
                    stmt.close();
                    conn.close();
                    
                    // Remove from local list
                    characters.removeIf(c -> c.getName().equals(name));
                    
                    System.out.println("Character deleted from database: " + name);
                    populateTable(); // Refresh table
                } catch (Exception e) {
                    System.out.println("Error deleting character from database: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, 
                        "Error deleting from database: " + e.getMessage(), 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a character to delete.");
        }
    }

    /**
     * Retrieves a character by its name.
     * @param name The name of the character.
     * @return The matching character or null if not found.
     */
    private Character getCharacterByName(String name) {
        return characters.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }
}

