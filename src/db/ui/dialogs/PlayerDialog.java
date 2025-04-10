package db.ui.dialogs;

import db.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for adding or editing a player.
 */
public class PlayerDialog extends JDialog {
    private boolean saved = false;
    private Player player;
    
    private JTextField loginField;
    private JTextField emailField;
    private JPasswordField passwordField;

    /**
     * Create a dialog for adding or editing a player.
     * @param player The player to edit, or null to create a new player.
     */
    public PlayerDialog(Player player) {
        this.player = player;
        boolean isNewPlayer = (player == null);
        
        setTitle(isNewPlayer ? "Add New Player" : "Edit Player");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        
        // Form panel with labels and fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Login ID field
        formPanel.add(new JLabel("Login ID:"));
        loginField = new JTextField(20);
        if (isNewPlayer) {
            // Only allow setting login ID for new players
            loginField.setEditable(true);
        } else {
            loginField.setText(player.getLoginId());
            loginField.setEditable(false); // Can't change login ID after creation
            loginField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
        }
        formPanel.add(loginField);
        
        // Email field
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        if (!isNewPlayer) {
            emailField.setText(player.getEmail());
        }
        formPanel.add(emailField);
        
        // Password field - always show for new players or when changing password
        formPanel.add(new JLabel(isNewPlayer ? "Password:" : "New Password:"));
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField);
        
        // If not new, add a note about empty password
        if (!isNewPlayer) {
            formPanel.add(new JLabel(""));
            formPanel.add(new JLabel("Leave empty to keep current password"));
        }
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (validateInput()) {
                savePlayer();
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
        String loginId = loginField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (player == null && loginId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login ID is required", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            loginField.requestFocus();
            return false;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        if (player == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new players", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Save the player data
     */
    private void savePlayer() {
        String loginId = loginField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (player == null) {
            // Create new player
            player = new Player(loginId, email, password);
        } else {
            // Update existing player
            player.setEmail(email);
            
            // Only update password if a new one was provided
            if (!password.isEmpty()) {
                player.setPassword(password);
            }
        }
    }

    /**
     * Check if the dialog was saved
     * @return true if the player was saved, false otherwise
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Get the player that was created or edited
     * @return the player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Validate email format using regex
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}

