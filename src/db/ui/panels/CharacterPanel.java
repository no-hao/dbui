package db.ui.panels;

import db.ui.dialogs.*; // For dialogs like CharacterFormDialog
import db.Location;import db.Ability;import db.Player;// Use fully qualified name for Character to avoid ambiguity with java.lang.Character
import db.Location;import db.Ability;import javax.swing.*; // For Swing components like JPanel, JTable, etc.
import db.Location;import db.Ability;import db.Player;import javax.swing.table.DefaultTableModel; // Add missing import
import db.Location;import db.Ability;import db.Player;import java.awt.*;    // For layouts like BorderLayout
import db.Location;import db.Ability;import db.Player;import java.util.List;
import db.Location;import db.Ability;import db.Player;import java.util.ArrayList;
import db.Location;import db.Ability;import db.Player;import db.Character;
import db.Location;import db.Ability;import db.Player;
import db.Location;import db.Ability;/**
import db.Location; * Panel for managing characters.
 * Provides functionality to view, add, edit, and delete characters.
 */
public class CharacterPanel extends JPanel {
    private JTable characterTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;

    // Mock data for characters (temporary until database integration)
    private List<Character> mockCharacters;

    public CharacterPanel() {
        setLayout(new BorderLayout());

        // Initialize mock data
        mockCharacters = new ArrayList<>();
        mockCharacters.add(new Character(1, "Archer", "player1@example.com", 100, 20, 15));
        mockCharacters.add(new Character(2, "Mage", "player2@example.com", 80, 10, 25));

        // Create table model and populate with mock data
        String[] columns = {"ID", "Name", "Player Email", "Max HP", "Strength", "Stamina"};
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
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                showCharacterForm(getCharacterById(id));
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
     * Populates the table with mock character data.
     */
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Character character : mockCharacters) {
            tableModel.addRow(new Object[]{
                character.getId(),
                character.getName(),
                character.getPlayerEmail(),
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
            if (character == null) {
                // Add new character
                updatedCharacter.setId(mockCharacters.size() + 1); // Generate ID
                mockCharacters.add(updatedCharacter);
            } else {
                // Update existing character
                int index = mockCharacters.indexOf(character);
                mockCharacters.set(index, updatedCharacter);
            }
            populateTable(); // Refresh table
        }
    }

    /**
     * Deletes the selected character from the list.
     */
    private void deleteSelectedCharacter() {
        int selectedRow = characterTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this character?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                mockCharacters.removeIf(c -> c.getId() == id);
                populateTable(); // Refresh table
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a character to delete.");
        }
    }

    /**
     * Retrieves a character by its ID.
     * @param id The ID of the character.
     * @return The matching character or null if not found.
     */
    private Character getCharacterById(int id) {
        return mockCharacters.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}

