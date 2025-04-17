package db.ui.panels;

import db.Player;
import db.Character;
import db.client.MessageClient;
import db.ui.dialogs.PlayerDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for managing players.
 * Provides functionality to view, add, edit, and delete players.
 */
public class PlayerPanel extends JPanel {
    private JTable playerTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;
    private JTextArea characterListArea;
    
    // Database-backed list of players
    private List<Player> mockPlayers = new ArrayList<>();
    private MessageClient messageClient;

    public PlayerPanel() {
        setLayout(new BorderLayout(5, 5));
        
        // Initialize message client for database communication
        messageClient = new MessageClient();
        
        // Initialize player list from database
        loadPlayersFromDatabase();
        
        // Create table model with non-editable cells
        String[] columns = {"Login ID", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing in table
            }
        };
        populateTable();
        
        // Create table with selection listener
        playerTable = new JTable(tableModel);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && playerTable.getSelectedRow() != -1) {
                displayPlayerDetails();
            }
        });
        
        // Panel for player list
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Players"));
        tablePanel.add(new JScrollPane(playerTable), BorderLayout.CENTER);
        
        // Panel for player details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Player Details"));
        
        characterListArea = new JTextArea(10, 30);
        characterListArea.setEditable(false);
        detailsPanel.add(new JScrollPane(characterListArea), BorderLayout.CENTER);
        
        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add Player");
        btnEdit = new JButton("Edit Selected");
        btnDelete = new JButton("Delete Selected");
        
        // Add action listeners for buttons
        btnAdd.addActionListener(e -> showPlayerDialog(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = playerTable.getSelectedRow();
            if (selectedRow != -1) {
                String loginId = (String) tableModel.getValueAt(selectedRow, 0);
                showPlayerDialog(getPlayerByLoginId(loginId));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a player to edit.");
            }
        });
        btnDelete.addActionListener(e -> deleteSelectedPlayer());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        // Add components to main panel
        add(tablePanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Populates the table with player data
     */
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Player player : mockPlayers) {
            tableModel.addRow(new Object[]{
                player.getLoginId(),
                player.getEmail()
            });
        }
    }
    
    /**
     * Displays details of the selected player including their characters
     */
    private void displayPlayerDetails() {
        int selectedRow = playerTable.getSelectedRow();
        if (selectedRow != -1) {
            String loginId = (String) tableModel.getValueAt(selectedRow, 0);
            Player player = getPlayerByLoginId(loginId);
            
            if (player != null) {
                StringBuilder details = new StringBuilder();
                details.append("Login ID: ").append(player.getLoginId()).append("\n");
                details.append("Email: ").append(player.getEmail()).append("\n\n");
                details.append("Characters:\n");
                
                // Get characters for this player
                List<Character> playerCharacters = getCharactersForPlayer(player.getLoginId());
                
                if (playerCharacters.isEmpty()) {
                    details.append("No characters found for this player.");
                } else {
                    for (Character character : playerCharacters) {
                        details.append("- ").append(character.getName())
                              .append(" (HP: ").append(character.getMaxPoints())
                              .append(", STR: ").append(character.getStrength())
                              .append(", STA: ").append(character.getStamina())
                              .append(")\n");
                    }
                }
                
                characterListArea.setText(details.toString());
            }
        }
    }
    
    /**
     * Shows a dialog to add or edit a player
     */
    private void showPlayerDialog(Player player) {
        PlayerDialog dialog = new PlayerDialog(player);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            Player updatedPlayer = dialog.getPlayer();
            if (player == null) {
                // Add new player to mock data
                mockPlayers.add(updatedPlayer);
                
                // Also add to the database
                try {
                    String params = String.join(",",
                        updatedPlayer.getLoginId(),
                        updatedPlayer.getEmail(),
                        updatedPlayer.getPassword(),
                        java.time.LocalDate.now().toString(),
                        "false", // isSilenced
                        "false", // isBlocked
                        "" // watchedBy (null)
                    );
                    
                    String response = messageClient.insert(
                        "PERSON",
                        "loginId,email,password,dateCreated",
                        params
                    );
                    
                    if (response.startsWith("SUCCESS:")) {
                        response = messageClient.insert(
                            "PLAYER",
                            "loginId,isSilenced,isBlocked,watchedBy",
                            String.join(",",
                                updatedPlayer.getLoginId(),
                                "false",
                                "false",
                                ""
                            )
                        );
                        
                        if (response.startsWith("SUCCESS:")) {
                            System.out.println("Player saved to database: " + updatedPlayer.getLoginId());
                        } else {
                            throw new Exception(response);
                        }
                    } else {
                        throw new Exception(response);
                    }
                } catch (Exception e) {
                    System.out.println("Error saving player to database: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, 
                        "Error saving to database: " + e.getMessage(), 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing player in mock data
                int index = mockPlayers.indexOf(player);
                mockPlayers.set(index, updatedPlayer);
                
                // Also update in the database
                try {
                    String response = messageClient.update(
                        "PERSON",
                        "email",
                        updatedPlayer.getEmail(),
                        "loginId",
                        "=",
                        updatedPlayer.getLoginId()
                    );
                    
                    if (response.startsWith("SUCCESS:")) {
                        System.out.println("Player updated in database: " + updatedPlayer.getLoginId());
                    } else {
                        throw new Exception(response);
                    }
                } catch (Exception e) {
                    System.out.println("Error updating player in database: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, 
                        "Error updating in database: " + e.getMessage(), 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            populateTable();
            
            // Select the added/edited player
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(updatedPlayer.getLoginId())) {
                    playerTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }
    
    /**
     * Deletes the selected player
     */
    private void deleteSelectedPlayer() {
        int selectedRow = playerTable.getSelectedRow();
        if (selectedRow != -1) {
            String loginId = (String) tableModel.getValueAt(selectedRow, 0);
            Player player = getPlayerByLoginId(loginId);
            
            // Check if player has characters
            List<Character> playerCharacters = getCharactersForPlayer(player.getLoginId());
            if (!playerCharacters.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "This player has " + playerCharacters.size() + " characters. Delete them too?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_CANCEL_OPTION
                );
                
                if (confirm == JOptionPane.CANCEL_OPTION) {
                    return;
                } else if (confirm == JOptionPane.YES_OPTION) {
                    // Delete characters from database
                    try {
                        String response = messageClient.delete(
                            "GAMECHARACTER",
                            "playerId",
                            "=",
                            player.getLoginId()
                        );
                        
                        if (response.startsWith("SUCCESS:")) {
                            System.out.println("Deleted characters for player: " + player.getLoginId());
                        } else {
                            throw new Exception(response);
                        }
                    } catch (Exception e) {
                        System.out.println("Error deleting characters: " + e.getMessage());
                        JOptionPane.showMessageDialog(this, 
                            "Error deleting characters: " + e.getMessage(), 
                            "Database Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this player?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Remove player from mock data
            mockPlayers.remove(player);
            
            // Also remove from database
            try {
                String response = messageClient.delete(
                    "PERSON",
                    "loginId",
                    "=",
                    player.getLoginId()
                );
                
                if (response.startsWith("SUCCESS:")) {
                    System.out.println("Player deleted from database: " + player.getLoginId());
                } else {
                    throw new Exception(response);
                }
            } catch (Exception e) {
                System.out.println("Error deleting player from database: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Error deleting from database: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            populateTable();
            characterListArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a player to delete.");
        }
    }
    
    /**
     * Finds a player by login ID
     */
    private Player getPlayerByLoginId(String loginId) {
        return mockPlayers.stream()
                .filter(p -> p.getLoginId().equals(loginId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get all characters for a specific player
     */
    private List<Character> getCharactersForPlayer(String playerId) {
        List<Character> playerCharacters = new ArrayList<>();
        
        try {
            String response = messageClient.select("GAMECHARACTER", "playerId", "=", playerId);
            if (response == null) {
                throw new Exception("No response from server");
            }
            
            if (!response.startsWith("ERROR:")) {
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
                            playerCharacters.add(character);
                        }
                    }
                }
            } else {
                System.err.println("Error from server: " + response.substring(6));
            }
            
            System.out.println("Loaded " + playerCharacters.size() + " characters for player: " + playerId);
        } catch (Exception e) {
            System.err.println("Error loading characters: " + e.getMessage());
        }
        
        return playerCharacters;
    }

    /**
     * Loads players from the database
     */
    private void loadPlayersFromDatabase() {
        mockPlayers.clear();
        try {
            String response = messageClient.select("PERSON", "*", "=", "");
            if (response == null) {
                throw new Exception("No response from server");
            }
            
            if (!response.startsWith("ERROR:")) {
                String[] lines = response.split("\n");
                if (lines.length > 1) { // Skip header row
                    for (int i = 1; i < lines.length; i++) {
                        String[] values = lines[i].split("\t");
                        if (values.length >= 4) {
                            String loginId = values[0];
                            String email = values[1];
                            String password = values[2];
                            java.time.LocalDate dateCreated = java.time.LocalDate.parse(values[3]);
                            
                            Player player = new Player(loginId, email, password);
                            player.setDateCreated(dateCreated);
                            
                            mockPlayers.add(player);
                        }
                    }
                }
            } else {
                throw new Exception(response.substring(6));
            }
            
            System.out.println("Loaded " + mockPlayers.size() + " players from database");
        } catch (Exception e) {
            System.err.println("Error loading players from database: " + e.getMessage());
            // Add fallback mock data if DB connection fails
            if (mockPlayers.isEmpty()) {
                mockPlayers.add(new Player("user1", "user1@example.com", "password1"));
                mockPlayers.add(new Player("user2", "user2@example.com", "password2"));
            }
        }
    }
}

