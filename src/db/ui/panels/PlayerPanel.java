package db.ui.panels;

import db.Player;
import db.Character;
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

    public PlayerPanel() {
        setLayout(new BorderLayout(5, 5));
        
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
                    java.sql.Connection conn = java.sql.DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                    
                    // Insert into PERSON table
                    java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO PERSON (loginId, email, password, dateCreated) VALUES (?, ?, ?, ?)");
                    stmt.setString(1, updatedPlayer.getLoginId());
                    stmt.setString(2, updatedPlayer.getEmail());
                    stmt.setString(3, updatedPlayer.getPassword());
                    stmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    stmt.executeUpdate();
                    
                    // Also insert into PLAYER table
                    stmt = conn.prepareStatement(
                        "INSERT INTO PLAYER (loginId, isSilenced, isBlocked, watchedBy) VALUES (?, ?, ?, ?)");
                    stmt.setString(1, updatedPlayer.getLoginId());
                    stmt.setBoolean(2, false);
                    stmt.setBoolean(3, false);
                    stmt.setString(4, null);
                    stmt.executeUpdate();
                    
                    stmt.close();
                    conn.close();
                    
                    System.out.println("Player saved to database: " + updatedPlayer.getLoginId());
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
                    java.sql.Connection conn = java.sql.DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                    
                    // Update PERSON table
                    java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE PERSON SET email = ?, password = ? WHERE loginId = ?");
                    stmt.setString(1, updatedPlayer.getEmail());
                    stmt.setString(2, updatedPlayer.getPassword());
                    stmt.setString(3, updatedPlayer.getLoginId());
                    stmt.executeUpdate();
                    
                    stmt.close();
                    conn.close();
                    
                    System.out.println("Player updated in database: " + updatedPlayer.getLoginId());
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
                        java.sql.Connection conn = java.sql.DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                        
                        java.sql.PreparedStatement stmt = conn.prepareStatement(
                            "DELETE FROM GAMECHARACTER WHERE playerId = ?");
                        stmt.setString(1, player.getLoginId());
                        int deletedRows = stmt.executeUpdate();
                        
                        stmt.close();
                        conn.close();
                        
                        System.out.println("Deleted " + deletedRows + " characters for player: " + player.getLoginId());
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
                java.sql.Connection conn = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
                
                // Delete from PERSON table
                java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM PERSON WHERE loginId = ?");
                stmt.setString(1, player.getLoginId());
                stmt.executeUpdate();
                
                stmt.close();
                conn.close();
                
                System.out.println("Player deleted from database: " + player.getLoginId());
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
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
            
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT name, playerId, locationId, maxPoints, currentPoints, stamina, strength " +
                "FROM GAMECHARACTER WHERE playerId = ?");
            stmt.setString(1, playerId);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("name");
                String playerIdFromDB = rs.getString("playerId");
                String locationId = rs.getString("locationId");
                int maxPoints = rs.getInt("maxPoints");
                int currentPoints = rs.getInt("currentPoints");
                int stamina = rs.getInt("stamina");
                int strength = rs.getInt("strength");
                
                Character character = new Character(
                    name, playerIdFromDB, maxPoints, currentPoints, stamina, strength);
                character.setLocationId(locationId);
                
                playerCharacters.add(character);
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("Loaded " + playerCharacters.size() + " characters for player: " + playerId);
        } catch (Exception e) {
            System.out.println("Error loading characters for player: " + e.getMessage());
        }
        
        return playerCharacters;
    }

    /**
     * Loads players from the database
     */
    private void loadPlayersFromDatabase() {
        mockPlayers.clear();
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gamedb", "root", "Password_27");
            
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT loginId, email, password, dateCreated FROM PERSON");
            
            while (rs.next()) {
                String loginId = rs.getString("loginId");
                String email = rs.getString("email");
                String password = rs.getString("password");
                java.sql.Date dateCreated = rs.getDate("dateCreated");
                
                Player player = new Player(loginId, email, password);
                // Convert java.sql.Date to java.time.LocalDate
                if (dateCreated != null) {
                    player.setDateCreated(dateCreated.toLocalDate());
                }
                
                mockPlayers.add(player);
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("Loaded " + mockPlayers.size() + " players from database");
        } catch (Exception e) {
            System.out.println("Error loading players from database: " + e.getMessage());
            // Add fallback mock data if DB connection fails
            if (mockPlayers.isEmpty()) {
                mockPlayers.add(new Player("user1", "user1@example.com", "password1"));
                mockPlayers.add(new Player("user2", "user2@example.com", "password2"));
            }
        }
    }
}

