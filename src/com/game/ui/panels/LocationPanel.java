package com.game.ui.panels;

import com.game.ui.Location;
import com.game.ui.dialogs.LocationFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing locations.
 * Provides functionality to view, add, edit, and delete locations.
 */
public class LocationPanel extends JPanel {
    private JTable locationTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;
    private JTextArea detailsArea;
    
    // Mock data for locations (temporary until database integration)
    private List<Location> mockLocations = new ArrayList<>();

    public LocationPanel() {
        setLayout(new BorderLayout(5, 5));
        
        // Initialize mock data
        mockLocations.add(new Location(1, "Castle", "Large fortress", "DUNGEON"));
        mockLocations.add(new Location(2, "Forest", "Dense woods with tall trees", "FOREST"));
        mockLocations.add(new Location(3, "Mountain Cave", "Dark cave in the mountains", "CAVE"));
        
        // Add exits between locations for demonstration
        mockLocations.get(0).addExit(mockLocations.get(1)); // Castle -> Forest
        mockLocations.get(1).addExit(mockLocations.get(0)); // Forest -> Castle
        mockLocations.get(1).addExit(mockLocations.get(2)); // Forest -> Mountain Cave
        
        // Create table model with non-editable cells
        String[] columns = {"ID", "Name", "Type", "Exits"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing in table
            }
        };
        populateTable();
        
        // Create table with selection listener
        locationTable = new JTable(tableModel);
        locationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        locationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && locationTable.getSelectedRow() != -1) {
                displayLocationDetails();
            }
        });
        
        // Panel for location list
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Locations"));
        tablePanel.add(new JScrollPane(locationTable), BorderLayout.CENTER);
        
        // Panel for location details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Location Details"));
        
        detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add Location");
        btnEdit = new JButton("Edit Selected");
        btnDelete = new JButton("Delete Selected");
        
        // Add action listeners for buttons
        btnAdd.addActionListener(e -> showLocationDialog(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = locationTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                showLocationDialog(getLocationById(id));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a location to edit.");
            }
        });
        btnDelete.addActionListener(e -> deleteSelectedLocation());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        // Add components to main panel
        add(tablePanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Populates the table with location data
     */
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Location location : mockLocations) {
            tableModel.addRow(new Object[]{
                location.getId(),
                location.getName(),
                location.getType(),
                location.getExitsAsString()
            });
        }
    }
    
    /**
     * Displays details of the selected location
     */
    private void displayLocationDetails() {
        int selectedRow = locationTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Location location = getLocationById(id);
            
            if (location != null) {
                StringBuilder details = new StringBuilder();
                details.append("Name: ").append(location.getName()).append("\n\n");
                details.append("Type: ").append(location.getType()).append("\n");
                details.append("Size: ").append(location.getSize()).append("\n\n");
                details.append("Description: ").append(location.getDescription()).append("\n\n");
                details.append("Exits:\n");
                
                List<Location> exits = location.getExits();
                if (exits.isEmpty()) {
                    details.append("None");
                } else {
                    for (Location exit : exits) {
                        details.append("- ").append(exit.getName())
                               .append(" (").append(exit.getType()).append(")\n");
                    }
                }
                
                detailsArea.setText(details.toString());
            }
        }
    }
    
    /**
     * Shows a dialog to add or edit a location
     */
    private void showLocationDialog(Location location) {
        LocationFormDialog dialog = new LocationFormDialog(location, mockLocations);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            Location updatedLocation = dialog.getLocationData();
            if (location == null) {
                // Add new location with a generated ID
                updatedLocation.setId(getNextLocationId());
                mockLocations.add(updatedLocation);
            } else {
                // Update existing location
                int index = mockLocations.indexOf(location);
                mockLocations.set(index, updatedLocation);
            }
            populateTable();
            
            // Select the added/edited location
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int)tableModel.getValueAt(i, 0) == updatedLocation.getId()) {
                    locationTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }
    
    /**
     * Deletes the selected location
     */
    private void deleteSelectedLocation() {
        int selectedRow = locationTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Location location = getLocationById(id);
            
            // Check if location is referenced by other locations as an exit
            boolean isReferenced = false;
            for (Location loc : mockLocations) {
                if (loc.hasExitTo(id)) {
                    isReferenced = true;
                    break;
                }
            }
            
            if (isReferenced) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "This location is referenced as an exit from other locations. Remove these references?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_CANCEL_OPTION
                );
                
                if (confirm == JOptionPane.CANCEL_OPTION) {
                    return;
                } else if (confirm == JOptionPane.YES_OPTION) {
                    // Remove this location from all exits
                    for (Location loc : mockLocations) {
                        loc.removeExit(location);
                    }
                }
            } else {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this location?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Remove location
            mockLocations.remove(location);
            populateTable();
            detailsArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a location to delete.");
        }
    }
    
    /**
     * Finds a location by ID
     */
    private Location getLocationById(int id) {
        return mockLocations.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Generates the next available location ID
     */
    private int getNextLocationId() {
        return mockLocations.stream()
                .mapToInt(Location::getId)
                .max()
                .orElse(0) + 1;
    }
}

