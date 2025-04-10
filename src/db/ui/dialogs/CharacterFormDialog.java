// CharacterFormDialog.java

package db.ui.dialogs;

import db.Character;
import db.Location;import db.Ability;import db.Player;import javax.swing.*; // For Swing components like JDialog, JLabel, etc.
import db.Location;import db.Ability;import db.Player;import java.awt.*;    // For layouts like GridLayout
import db.Location;import db.Ability;import db.Player;
import db.Location;import db.Ability;
import db.Location;public class CharacterFormDialog extends JDialog {
    private boolean saved = false;
    private Character character;

    public CharacterFormDialog(Character character) {
        setLayout(new GridLayout(6, 2));
        
        // Player selection
        JComboBox<String> playerCombo = new JComboBox<>(new String[]{"player1", "player2"});
        
        // Numeric fields with validation
        JSpinner maxHpSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 1000, 1));
        JSpinner strSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JSpinner staSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        
        add(new JLabel("Player:"));
        add(playerCombo);
        add(new JLabel("Name:"));
        JTextField nameField = new JTextField(20);
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
            this.character = new Character(
                0, 
                nameField.getText(),
                (String) playerCombo.getSelectedItem(),
                (int) maxHpSpinner.getValue(),
                (int) strSpinner.getValue(),
                (int) staSpinner.getValue()
            );
            saved = true;
            dispose();
        });
        
        add(saveButton);
        setSize(400, 300);
    }

    public boolean isSaved() { return saved; }
    public Character getCharacter() { return character; }
}

