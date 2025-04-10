package com.game.ui.dialogs;

import com.game.ui.Location;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for adding or editing a location.
 */
public class LocationFormDialog extends JDialog {
    private boolean saved = false;
    private Location location;
    
    private JTextField nameField;
    private JTextField descriptionField;
    private JComboBox<String> typeCombo;
    private JSpinner sizeSpinner;
    private JList<Location> exitsList;
    private DefaultListModel<Location> exitsModel;
    private List<Location> availableLocations;
    
    /**
     * Create a dialog for adding or editing a location.
     * @param location The location to edit, or null to create a new location.
     * @param allLocations List of all available locations (for selecting exits)
     */
    public LocationFormDialog(Location location, List<Location> allLocations) {
        this.location = location;
        this.availableLocations = allLocations;
        boolean isNewLocation = (location == null);
        
        setTitle(isNewLocation ? "Add New Location" : "Edit Location");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Name field
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        if (!isNewLocation) {
            nameField.setText(location.getName());
        }
        formPanel.add(nameField);
        
        // Description field
        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField(20);
        if (!isNewLocation) {
            descriptionField.setText(location.getDescription());
        }
        formPanel.add(descriptionField);
        
        // Type dropdown
        formPanel.add(new JLabel("Type:"));
        String[] types = {"TOWN", "DUNGEON", "FOREST", "MOUNTAIN", "CAVE", "CASTLE"};
        typeCombo = new JComboBox<>(types);
        if (!isNewLocation) {
            typeCombo.setSelectedItem(location.getType());
        }
        formPanel.add(typeCombo);
        
        // Size spinner
        formPanel.add(new JLabel("Size:"));
        sizeSpinner = new JSpinner(new SpinnerNumberModel(
            isNewLocation ? 1 : location.getSize(), 
            1, 100, 1
        ));
        formPanel.add(sizeSpinner);
        
        // Exits section (only for editing existing locations)
        JPanel exitsPanel = new JPanel(new BorderLayout(5, 5));
        exitsPanel.setBorder(BorderFactory.createTitledBorder("Exits"));
        
        // Model for exits list
        exitsModel = new DefaultListModel<>();
        exitsList = new JList<>(exitsModel);
        exitsList.setCellRenderer(new LocationListCellRenderer());
        
        if (!isNewLocation) {
            // Add existing exits to the model
            for (Location exit : location.getExits()) {
                exitsModel.addElement(exit);
            }
        }
        
        exitsPanel.add(new JScrollPane(exitsList), BorderLayout.CENTER);
        
        // Buttons for managing exits
        JPanel exitsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addExitButton = new JButton("Add Exit");
        addExitButton.addActionListener(e -> addExit());
        
        JButton removeExitButton = new JButton("Remove Exit");
        removeExitButton.addActionListener(e -> removeExit());
        
        exitsButtonPanel.add(addExitButton);
        exitsButtonPanel.add(removeExitButton);
        
        exitsPanel.add(exitsButtonPanel, BorderLayout.SOUTH);
        
        // Button panel for form
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (validateInput()) {
                saveLocation();
                saved = true;
                dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to dialog
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Only add exits panel for editing existing locations
        if (!isNewLocation) {
            mainPanel.add(exitsPanel, BorderLayout.CENTER);
        }
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog properties
        setSize(500, isNewLocation ? 300 : 500);
        setResizable(false);
        setLocationRelativeTo(null);
    }
    
    /**
     * Add an exit to the location
     */
    private void addExit() {
        // Create a list of locations that are not already exits and not the current location
        DefaultListModel<Location> availableExitsModel = new DefaultListModel<>();
        for (Location loc : availableLocations) {
            if (location.getId() != loc.getId() && !location.hasExitTo(loc.getId())) {
                availableExitsModel.addElement(loc);
            }
        }
        
        if (availableExitsModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No available locations to add as exits.", 
                "No Locations", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a list with the available locations
        JList<Location> availableList = new JList<>(availableExitsModel);
        availableList.setCellRenderer(new LocationListCellRenderer());
        availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Show dialog to select a location
        int result = JOptionPane.showConfirmDialog(
            this, 
            new JScrollPane(availableList), 
            "Select Exit Location", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION && availableList.getSelectedValue() != null) {
            exitsModel.addElement(availableList.getSelectedValue());
        }
    }
    
    /**
     * Remove an exit from the location
     */
    private void removeExit() {
        int selectedIndex = exitsList.getSelectedIndex();
        if (selectedIndex != -1) {
            exitsModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an exit to remove.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Validate user input
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Location name is required", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Location description is required", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            descriptionField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Save the location data
     */
    private void saveLocation() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        int size = (Integer) sizeSpinner.getValue();
        
        if (location == null) {
            // Create new location with a temporary ID
            location = new Location(0, name, description, type, size, null);
        } else {
            // Update existing location
            location.setName(name);
            location.setDescription(description);
            location.setType(type);
            location.setSize(size);
            
            // Clear and re-add all exits
            for (Location exit : location.getExits()) {
                location.removeExit(exit);
            }
            
            for (int i = 0; i < exitsModel.size(); i++) {
                location.addExit(exitsModel.get(i));
            }
        }
    }
    
    /**
     * Check if the dialog was saved
     * @return true if the dialog was saved, false otherwise
     */
    public boolean isSaved() {
        return saved;
    }
    
    /**
     * Get the location that was created or edited
     * @return the location object
     * 
     * Renamed from getLocation() to avoid conflict with Component.getLocation()
     */
    public Location getLocationData() {
        return location;
    }
    
    /**
     * Custom cell renderer for Location objects in a JList
     */
    private class LocationListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Location) {
                Location loc = (Location) value;
                setText(loc.getName() + " (" + loc.getType() + ")");
            }
            
            return this;
        }
    }
} 