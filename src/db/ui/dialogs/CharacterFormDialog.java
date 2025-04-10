/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Character Form Dialog - Form for adding and editing game characters
 * 
 * @author [YOUR NAME HERE]
 */
package db.ui.dialogs;

import db.Character;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing a character.
 */
public class CharacterFormDialog extends JDialog {
    private boolean saved = false;
    private Character character;

    public CharacterFormDialog(Character character) {
        setTitle(character == null ? "Add Character" : "Edit Character: " + character.getName());
        setLayout(new GridLayout(6, 2));
        
        // Retrieve actual players from database via DatabaseManager
        final JComboBox<String> playerCombo;
        
        java.util.Vector<String> playerIds = new java.util.Vector<>();
        playerIds.add("player1");
        playerIds.add("player2"); // Default fallback values
        
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT loginId FROM PLAYER");
            
            playerIds.clear(); // Clear default values if DB connection successful
            while(rs.next()) {
                playerIds.add(rs.getString("loginId"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error loading players: " + e.getMessage());
        }
        
        playerCombo = new JComboBox<>(playerIds);
        
        // Character name field
        JTextField nameField = new JTextField(20);
        
        // Numeric fields with validation
        JSpinner maxHpSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 1000, 1));
        JSpinner strSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JSpinner staSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        
        // Set values if editing an existing character
        if (character != null) {
            nameField.setText(character.getName());
            nameField.setEditable(false); // Can't change name as it's the primary key
            playerCombo.setSelectedItem(character.getPlayerId());
            maxHpSpinner.setValue(character.getMaxPoints());
            strSpinner.setValue(character.getStrength());
            staSpinner.setValue(character.getStamina());
        }
        
        add(new JLabel("Player:"));
        add(playerCombo);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Max HP:"));
        add(maxHpSpinner);
        add(new JLabel("Strength:"));
        add(strSpinner);
        add(new JLabel("Stamina:"));
        add(staSpinner);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty");
                return;
            }
            
            if (character == null) {
                // Check if character name already exists
                try {
                    java.sql.Connection conn = java.sql.DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                    
                    java.sql.PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM GAMECHARACTER WHERE name = ?");
                    checkStmt.setString(1, nameField.getText());
                    java.sql.ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    
                    rs.close();
                    checkStmt.close();
                    conn.close();
                    
                    if (count > 0) {
                        JOptionPane.showMessageDialog(this, "A character with this name already exists");
                        return;
                    }
                } catch (Exception ex) {
                    System.out.println("Error checking character name: " + ex.getMessage());
                }
            }
            
            // Create a new Character object
            String selectedPlayer = (String) playerCombo.getSelectedItem();
            int maxHp = (int) maxHpSpinner.getValue();
            int strength = (int) strSpinner.getValue();
            int stamina = (int) staSpinner.getValue();
            
            this.character = new Character(
                nameField.getText(), 
                selectedPlayer,
                maxHp,  // maxPoints
                maxHp,  // currentPoints (start at full)
                stamina,
                strength
            );
            
            saved = true;
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(new JLabel("")); // Empty cell for spacing
        add(buttonPanel);
        
        pack();
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModal(true);
    }

    public boolean isSaved() { return saved; }
    public Character getCharacter() { return character; }
}

