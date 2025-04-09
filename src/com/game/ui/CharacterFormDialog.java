package com.game.ui;

import javax.swing.*;

public class CharacterFormDialog extends JDialog {
    private boolean saved = false;
    
    public CharacterFormDialog(Character character) {
        // Implement form fields and buttons here
        setSize(400, 300);
        setModal(true);
    }
    
    public boolean isSaved() { return saved; }
    public Character getCharacter() { 
        // Return populated Character object
        return new Character(0, "New", "email@example.com", 100, 10, 10);
    }
}

