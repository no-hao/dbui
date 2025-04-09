package com.game.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for the Game Database Manager UI.
 * This sets up the main application window with tabs for Characters, Players, Abilities, and Locations (if applicable).
 */
public class DatabaseUI extends JFrame {
    private JTabbedPane mainTabs;

    public DatabaseUI() {
        super("Game Database Manager"); // Set window title
        setLayout(new BorderLayout());
        setSize(1200, 800); // Set default window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on exit

        // Initialize the tabbed pane
        mainTabs = new JTabbedPane();

        // Add tabs for each display
        mainTabs.addTab("Characters", new CharacterPanel());
        mainTabs.addTab("Players", new PlayerPanel());
        mainTabs.addTab("Abilities", new AbilityPanel());

        // Optional: Add Locations tab if this is a group of 4
        boolean isGroupOfFour = true; // Change this based on your group size
        if (isGroupOfFour) {
            mainTabs.addTab("Locations", new LocationPanel());
        }

        // Add the tabbed pane to the frame
        add(mainTabs, BorderLayout.CENTER);
    }

    /**
     * Entry point for the application.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseUI ui = new DatabaseUI();
            ui.setVisible(true); // Make the UI visible
        });
    }
}

