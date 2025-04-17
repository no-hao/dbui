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
import db.client.MessageClient;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing a character.
 */
public class CharacterFormDialog extends JDialog {
    private boolean saved = false;
    private Character character;
    private MessageClient messageClient;

    public CharacterFormDialog(Character character) {
        setTitle(character == null ? "Add Character" : "Edit Character: " + character.getName());
        setLayout(new GridLayout(6, 2));
        
        messageClient = new MessageClient();
        
        // Retrieve actual players from database via MessageClient
        final JComboBox<String> playerCombo;
        
        java.util.Vector<String> playerIds = new java.util.Vector<>();
        playerIds.add("player1");
        playerIds.add("player2"); // Default fallback values
        
        try {
            String response = messageClient.select("PLAYER", "loginId", "=", "");
            if (!response.startsWith("ERROR:")) {
                String[] lines = response.split("\n");
                if (lines.length > 1) { // Skip header row
                    playerIds.clear(); // Clear default values if DB connection successful
                    for (int i = 1; i < lines.length; i++) {
                        playerIds.add(lines[i].trim());
                    }
                }
            }
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
                    String response = messageClient.select("GAMECHARACTER", "name", "=", nameField.getText());
                    if (!response.startsWith("ERROR:") && response.split("\n").length > 1) {
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
                stamina,
                strength
            );
            
            saved = true;
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(new JLabel(""));
        add(buttonPanel);
        
        pack();
        setLocationRelativeTo(null);
        setModal(true);
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public Character getCharacter() {
        return character;
    }
}

