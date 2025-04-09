package com.game.ui;

/**
 * Represents a game character.
 */
public class Character {
    private int id;
    private String name;
    private String playerEmail;
    private int maxHp;
    private int strength;
    private int stamina;

    public Character(int id, String name, String playerEmail, int maxHp, int strength, int stamina) {
        this.id = id;
        this.name = name;
        this.playerEmail = playerEmail;
        this.maxHp = maxHp;
        this.strength = strength;
        this.stamina = stamina;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPlayerEmail() { return playerEmail; }
    public void setPlayerEmail(String playerEmail) { this.playerEmail = playerEmail; }
    
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    
    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }
    
    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }
}

