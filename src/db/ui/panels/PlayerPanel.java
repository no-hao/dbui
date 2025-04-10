package db.ui.panels;

import db.*;
import db.Location;import db.Ability;import db.Player;import db.Character;import db.*;
import db.Location;import db.Ability;import db.Player;import db.Character;import db.ui.dialogs.*;
import db.Location;import db.Ability;import db.Player;import db.Character;
import db.Location;import db.Ability;import db.Player;import javax.swing.*;
import db.Location;import db.Ability;import db.Player;import db.Character;import javax.swing.table.DefaultTableModel;
import db.Location;import db.Ability;import db.Player;import db.Character;import java.awt.*;
import db.Location;import db.Ability;import db.Player;import db.Character;import java.util.ArrayList;
import db.Location;import db.Ability;import db.Player;import db.Character;import java.util.List;
import db.Location;import db.Ability;import db.Player;import db.Character;import java.util.stream.Collectors;
import db.Location;import db.Ability;import db.Player;import db.Character;
import db.Location;import db.Ability;import db.Player;/**
import db.Location;import db.Ability; * Panel for managing players.
import db.Location; * Provides functionality to view, add, edit, and delete players.
 */
public class PlayerPanel extends JPanel {
    private JTable playerTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;
    private JTextArea characterListArea;
    
    // Mock data for players (temporary until database integration)
    private List<Player> mockPlayers = new ArrayList<>();
    // Mock list of characters for demonstration
    private List<Character> mockCharacters = new ArrayList<>();

    public PlayerPanel() {
        setLayout(new BorderLayout(5, 5));
        
        // Initialize mock data
        mockPlayers.add(new Player("user1", "user1@example.com", "password1"));
        mockPlayers.add(new Player("user2", "user2@example.com", "password2"));
        
        // Mock characters for players
        mockCharacters.add(new Character(1, "Warrior", "user1@example.com", 150, 30, 25));
        mockCharacters.add(new Character(2, "Rogue", "user1@example.com", 100, 25, 35));
        mockCharacters.add(new Character(3, "Mage", "user2@example.com", 80, 15, 20));
        
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
                List<Character> playerCharacters = getCharactersForPlayer(player.getEmail());
                
                if (playerCharacters.isEmpty()) {
                    details.append("No characters found for this player.");
                } else {
                    for (Character character : playerCharacters) {
                        details.append("- ").append(character.getName())
                              .append(" (HP: ").append(character.getMaxHp())
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
                // Add new player
                mockPlayers.add(updatedPlayer);
            } else {
                // Update existing player
                int index = mockPlayers.indexOf(player);
                mockPlayers.set(index, updatedPlayer);
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
            List<Character> playerCharacters = getCharactersForPlayer(player.getEmail());
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
                    // Remove characters
                    mockCharacters.removeIf(c -> c.getPlayerEmail().equals(player.getEmail()));
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
            
            // Remove player
            mockPlayers.remove(player);
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
    private List<Character> getCharactersForPlayer(String playerEmail) {
        return mockCharacters.stream()
                .filter(c -> c.getPlayerEmail().equals(playerEmail))
                .collect(Collectors.toList());
    }
}

