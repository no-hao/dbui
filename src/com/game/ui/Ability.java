package com.game.ui;

/**
 * Model class for game abilities
 */
public class Ability {
    private int id;
    private String name;
    private String targetStat;
    private int amount;
    private int durationToExecute;
    private double cooldown;
    private int uses;

    /**
     * Create a new ability
     */
    public Ability(int id, String name, String targetStat, int amount, double cooldown) {
        this.id = id;
        this.name = name;
        this.targetStat = targetStat;
        this.amount = amount;
        this.cooldown = cooldown;
        this.durationToExecute = 1;
        this.uses = -1; // Unlimited by default
    }
    
    /**
     * Create a new ability with all parameters
     */
    public Ability(int id, String name, String targetStat, int amount, 
                  int durationToExecute, double cooldown, int uses) {
        this.id = id;
        this.name = name;
        this.targetStat = targetStat;
        this.amount = amount;
        this.durationToExecute = durationToExecute;
        this.cooldown = cooldown;
        this.uses = uses;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTargetStat() { return targetStat; }
    public int getAmount() { return amount; }
    public int getDurationToExecute() { return durationToExecute; }
    public double getCooldown() { return cooldown; }
    public int getUses() { return uses; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTargetStat(String targetStat) { this.targetStat = targetStat; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setDurationToExecute(int durationToExecute) { this.durationToExecute = durationToExecute; }
    public void setCooldown(double cooldown) { this.cooldown = cooldown; }
    public void setUses(int uses) { this.uses = uses; }
    
    // Utility methods for UI display
    public String getAffectedStat() { return targetStat; } // For compatibility with existing code
    public int getModifier() { return amount; } // For compatibility with existing code
}

