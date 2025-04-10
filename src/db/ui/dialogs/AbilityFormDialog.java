package db.ui.dialogs;

import db.*;
import db.Location;import db.Ability;import db.Player;import db.Character;import javax.swing.*;
import db.Location;import db.Ability;import db.Player;import db.Character;import java.awt.*;
import db.Location;import db.Ability;import db.Player;import db.Character;
import db.Location;import db.Ability;import db.Player;/**
import db.Location;import db.Ability; * Dialog for adding or editing an ability.
import db.Location; */
public class AbilityFormDialog extends JDialog {
    private boolean saved = false;
    private Ability ability;
    
    private JTextField nameField;
    private JComboBox<String> statCombo;
    private JSpinner amountSpinner;
    private JSpinner durationSpinner;
    private JSpinner cooldownSpinner;
    private JSpinner usesSpinner;
    
    /**
     * Create a dialog for adding or editing an ability.
     * @param ability The ability to edit, or null to create a new ability.
     */
    public AbilityFormDialog(Ability ability) {
        this.ability = ability;
        boolean isNewAbility = (ability == null);
        
        setTitle(isNewAbility ? "Add New Ability" : "Edit Ability");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Name field
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        if (!isNewAbility) {
            nameField.setText(ability.getName());
        }
        formPanel.add(nameField);
        
        // Target stat dropdown
        formPanel.add(new JLabel("Target Stat:"));
        String[] stats = {"STR", "HP", "STA", "DEF", "SPD"};
        statCombo = new JComboBox<>(stats);
        if (!isNewAbility) {
            statCombo.setSelectedItem(ability.getTargetStat());
        }
        formPanel.add(statCombo);
        
        // Amount spinner (can be positive or negative)
        formPanel.add(new JLabel("Effect Amount:"));
        amountSpinner = new JSpinner(new SpinnerNumberModel(
            isNewAbility ? 0 : ability.getAmount(), 
            -100, 100, 1
        ));
        formPanel.add(amountSpinner);
        
        // Duration spinner
        formPanel.add(new JLabel("Duration to Execute:"));
        durationSpinner = new JSpinner(new SpinnerNumberModel(
            isNewAbility ? 1 : ability.getDurationToExecute(),
            1, 60, 1
        ));
        formPanel.add(durationSpinner);
        
        // Cooldown spinner (decimal allowed)
        formPanel.add(new JLabel("Cooldown (seconds):"));
        cooldownSpinner = new JSpinner(new SpinnerNumberModel(
            isNewAbility ? 1.0 : ability.getCooldown(),
            0.1, 60.0, 0.1
        ));
        // Set editor to display decimal places
        JSpinner.NumberEditor cooldownEditor = new JSpinner.NumberEditor(cooldownSpinner, "0.0");
        cooldownSpinner.setEditor(cooldownEditor);
        formPanel.add(cooldownSpinner);
        
        // Uses spinner (-1 means unlimited)
        formPanel.add(new JLabel("Uses (-1 = unlimited):"));
        usesSpinner = new JSpinner(new SpinnerNumberModel(
            isNewAbility ? -1 : ability.getUses(),
            -1, 999, 1
        ));
        formPanel.add(usesSpinner);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (validateInput()) {
                saveAbility();
                saved = true;
                dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog properties
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }
    
    /**
     * Validate user input
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        String name = nameField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ability name is required", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Save the ability data
     */
    private void saveAbility() {
        String name = nameField.getText().trim();
        String targetStat = (String) statCombo.getSelectedItem();
        int amount = (Integer) amountSpinner.getValue();
        int duration = (Integer) durationSpinner.getValue();
        double cooldown = ((Double) cooldownSpinner.getValue());
        int uses = (Integer) usesSpinner.getValue();
        
        if (ability == null) {
            // Create new ability with temporary ID
            ability = new Ability(0, name, targetStat, amount, duration, cooldown, uses);
        } else {
            // Update existing ability
            ability.setName(name);
            ability.setTargetStat(targetStat);
            ability.setAmount(amount);
            ability.setDurationToExecute(duration);
            ability.setCooldown(cooldown);
            ability.setUses(uses);
        }
    }
    
    /**
     * Check if the dialog was saved
     * @return true if the ability was saved, false otherwise
     */
    public boolean isSaved() {
        return saved;
    }
    
    /**
     * Get the ability that was created or edited
     * @return the ability object
     */
    public Ability getAbility() {
        return ability;
    }
} 