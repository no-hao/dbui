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
import db.client.MessageClient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing characters.
 * Provides functionality to view, add, edit, and delete characters.
 */
public class CharacterPanel extends JPanel {
    private JTable characterTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;
    private MessageClient messageClient;

    // Database-backed list of characters
    private List<Character> characters;

    public CharacterPanel() {
        setLayout(new BorderLayout());
        messageClient = new MessageClient();

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
            if (selectedRow >= 0) {
                showCharacterForm(characters.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a character to edit");
            }
        });
        btnDelete.addActionListener(e -> {
            int selectedRow = characterTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this character?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteCharacter(characters.get(selectedRow));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a character to delete");
            }
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads characters from the database using the message server
     */
    private void loadCharactersFromDatabase() {
        characters.clear();
        try {
            String response = messageClient.select("GAMECHARACTER", "*", "=", "");
            if (response.startsWith("ERROR:")) {
                throw new Exception(response.substring(6));
            }

            // Parse the response (tab-separated values)
            String[] lines = response.split("\n");
            if (lines.length > 1) { // Skip header row
                for (int i = 1; i < lines.length; i++) {
                    String[] values = lines[i].split("\t");
                    if (values.length >= 7) {
                        Character character = new Character(
                            values[0], // name
                            values[1], // playerId
                            Integer.parseInt(values[2]), // maxPoints
                            Integer.parseInt(values[3]), // currentPoints
                            Integer.parseInt(values[4]), // stamina
                            Integer.parseInt(values[5]), // strength
                            values[6]  // locationId
                        );
                        characters.add(character);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading characters from database: " + e.getMessage());
            // Add fallback mock data if DB connection fails
            if (characters.isEmpty()) {
                characters.add(new Character("Archer", "user1", 100, 20, 15));
                characters.add(new Character("Mage", "user2", 80, 10, 25));
            }
        }
    }

    /**
     * Populates the table with character data
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
     * Shows a form dialog for adding or editing a character
     * @param character The character to edit (null if adding a new one)
     */
    private void showCharacterForm(Character character) {
        CharacterFormDialog dialog = new CharacterFormDialog(character);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Character updatedCharacter = dialog.getCharacter();
            
            try {
                if (character == null) {
                    // Use stored procedure to create character
                    String params = String.join(",",
                        updatedCharacter.getName(),
                        updatedCharacter.getPlayerId(),
                        String.valueOf(updatedCharacter.getMaxHp()),
                        String.valueOf(updatedCharacter.getMaxHp()), // currentPoints = maxPoints
                        String.valueOf(updatedCharacter.getStamina()),
                        String.valueOf(updatedCharacter.getStrength()),
                        "default" // default location
                    );
                    
                    try {
                        String response = messageClient.callProcedure("create_character", params);
                        if (response.startsWith("SUCCESS:")) {
                            characters.add(updatedCharacter);
                            populateTable();
                            JOptionPane.showMessageDialog(this, "Character created successfully");
                        } else {
                            throw new Exception(response);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error creating character: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Update existing character
                    try {
                        String response = messageClient.update(
                            "GAMECHARACTER",
                            "currentPoints",
                            String.valueOf(updatedCharacter.getMaxHp()),
                            "name",
                            "=",
                            updatedCharacter.getName()
                        );
                        
                        if (response.startsWith("SUCCESS:")) {
                            int index = characters.indexOf(character);
                            if (index >= 0) {
                                characters.set(index, updatedCharacter);
                                populateTable();
                                JOptionPane.showMessageDialog(this, "Character updated successfully");
                            }
                        } else {
                            throw new Exception(response);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error updating character: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving character: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes a character from the database
     * @param character The character to delete
     */
    private void deleteCharacter(Character character) {
        try {
            String response = messageClient.delete(
                "GAMECHARACTER",
                "name",
                "=",
                character.getName()
            );
            
            if (response.startsWith("SUCCESS:")) {
                characters.remove(character);
                populateTable();
            } else {
                throw new Exception(response);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error deleting character: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

