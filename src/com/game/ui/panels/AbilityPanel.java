package com.game.ui.panels;

import com.game.ui.Ability;
import com.game.ui.dialogs.AbilityFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing abilities.
 * Provides functionality to view, add, edit, and delete abilities.
 */
public class AbilityPanel extends JPanel {
    private JTable abilityTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;
    private JTextArea detailsArea;
    
    // Mock data for abilities (temporary until database integration)
    private List<Ability> mockAbilities = new ArrayList<>();

    public AbilityPanel() {
        setLayout(new BorderLayout(5, 5));
        
        // Initialize mock data
        mockAbilities.add(new Ability(1, "Fireball", "HP", -15, 2, 5.0, 10));
        mockAbilities.add(new Ability(2, "Heal", "HP", 20, 3, 10.0, 5));
        mockAbilities.add(new Ability(3, "Strength Boost", "STR", 5, 1, 30.5, -1));
        
        // Create table model with non-editable cells
        String[] columns = {"ID", "Name", "Affects", "Effect", "Cooldown"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing in table
            }
        };
        populateTable();
        
        // Create table with selection listener
        abilityTable = new JTable(tableModel);
        abilityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        abilityTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && abilityTable.getSelectedRow() != -1) {
                displayAbilityDetails();
            }
        });
        
        // Panel for ability list
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Abilities"));
        tablePanel.add(new JScrollPane(abilityTable), BorderLayout.CENTER);
        
        // Panel for ability details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Ability Details"));
        
        detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add Ability");
        btnEdit = new JButton("Edit Selected");
        btnDelete = new JButton("Delete Selected");
        
        // Add action listeners for buttons
        btnAdd.addActionListener(e -> showAbilityDialog(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = abilityTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                showAbilityDialog(getAbilityById(id));
            } else {
                JOptionPane.showMessageDialog(this, "Please select an ability to edit.");
            }
        });
        btnDelete.addActionListener(e -> deleteSelectedAbility());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        // Add components to main panel
        add(tablePanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Populates the table with ability data
     */
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Ability ability : mockAbilities) {
            tableModel.addRow(new Object[]{
                ability.getId(),
                ability.getName(),
                ability.getTargetStat(),
                (ability.getAmount() >= 0 ? "+" : "") + ability.getAmount(),
                ability.getCooldown() + "s"
            });
        }
    }
    
    /**
     * Displays details of the selected ability
     */
    private void displayAbilityDetails() {
        int selectedRow = abilityTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Ability ability = getAbilityById(id);
            
            if (ability != null) {
                StringBuilder details = new StringBuilder();
                details.append("Name: ").append(ability.getName()).append("\n\n");
                details.append("Target Stat: ").append(ability.getTargetStat()).append("\n");
                details.append("Effect Amount: ").append(ability.getAmount()).append("\n");
                details.append("Duration to Execute: ").append(ability.getDurationToExecute()).append(" seconds\n");
                details.append("Cooldown: ").append(ability.getCooldown()).append(" seconds\n");
                
                if (ability.getUses() == -1) {
                    details.append("Uses: Unlimited");
                } else {
                    details.append("Uses: ").append(ability.getUses());
                }
                
                detailsArea.setText(details.toString());
            }
        }
    }
    
    /**
     * Shows a dialog to add or edit an ability
     */
    private void showAbilityDialog(Ability ability) {
        AbilityFormDialog dialog = new AbilityFormDialog(ability);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            Ability updatedAbility = dialog.getAbility();
            if (ability == null) {
                // Add new ability with a generated ID
                updatedAbility.setId(getNextAbilityId());
                mockAbilities.add(updatedAbility);
            } else {
                // Update existing ability
                int index = mockAbilities.indexOf(ability);
                mockAbilities.set(index, updatedAbility);
            }
            populateTable();
            
            // Select the added/edited ability
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int)tableModel.getValueAt(i, 0) == updatedAbility.getId()) {
                    abilityTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }
    
    /**
     * Deletes the selected ability
     */
    private void deleteSelectedAbility() {
        int selectedRow = abilityTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Ability ability = getAbilityById(id);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the ability '" + ability.getName() + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                mockAbilities.remove(ability);
                populateTable();
                detailsArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ability to delete.");
        }
    }
    
    /**
     * Finds an ability by ID
     */
    private Ability getAbilityById(int id) {
        return mockAbilities.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Generates the next available ability ID
     */
    private int getNextAbilityId() {
        return mockAbilities.stream()
                .mapToInt(Ability::getId)
                .max()
                .orElse(0) + 1;
    }
}

