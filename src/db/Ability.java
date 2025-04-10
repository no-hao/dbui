/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Ability Model - Represents an ability entity
 * 
 * @author [YOUR NAME HERE]
 */
package db;

/**
 * Represents an ability in the database.
 * Maps to the ABILITY table.
 */
public class Ability {
    // ABILITY table fields
    private String name;            // Primary key
    private String targetStat;
    private int amount;
    private int durationToExecute;
    private double cooldown;
    private int uses;
    
    // Additional fields for UI
    private int id; // Used only in UI, not stored in database

    /**
     * Creates a new ability with minimal required information.
     * 
     * @param name Ability name (primary key)
     * @param targetStat Target stat affected by this ability
     * @param amount Amount of effect on the target stat
     * @param durationToExecute Duration to execute the ability
     * @param cooldown Cooldown period before ability can be used again
     * @param uses Number of uses (-1 for unlimited)
     */
    public Ability(String name, String targetStat, int amount, int durationToExecute, double cooldown, int uses) {
        this.name = name;
        this.targetStat = targetStat;
        this.amount = amount;
        this.durationToExecute = durationToExecute;
        this.cooldown = cooldown;
        this.uses = uses;
    }
    
    /**
     * Creates a new ability with UI ID for display purposes.
     * 
     * @param id UI identifier (not stored in DB)
     * @param name Ability name (primary key)
     * @param targetStat Target stat affected by this ability
     * @param amount Amount of effect on the target stat
     * @param durationToExecute Duration to execute the ability
     * @param cooldown Cooldown period before ability can be used again
     * @param uses Number of uses (-1 for unlimited)
     */
    public Ability(int id, String name, String targetStat, int amount, int durationToExecute, double cooldown, int uses) {
        this(name, targetStat, amount, durationToExecute, cooldown, uses);
        this.id = id;
    }
    
    // For backward compatibility with existing code
    public Ability(int id, String name, String targetStat, int amount, double cooldown) {
        this(id, name, targetStat, amount, 1, cooldown, -1); // Default duration 1, unlimited uses
    }

    // Getters
    public String getName() { return name; }
    public String getTargetStat() { return targetStat; }
    public int getAmount() { return amount; }
    public int getDurationToExecute() { return durationToExecute; }
    public double getCooldown() { return cooldown; }
    public int getUses() { return uses; }
    public int getId() { return id; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setTargetStat(String targetStat) { this.targetStat = targetStat; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setDurationToExecute(int durationToExecute) { this.durationToExecute = durationToExecute; }
    public void setCooldown(double cooldown) { this.cooldown = cooldown; }
    public void setUses(int uses) { this.uses = uses; }
    public void setId(int id) { this.id = id; }
    
    // Utility methods for UI display
    public String getAffectedStat() { return targetStat; } // For compatibility with existing code
    public int getModifier() { return amount; } // For compatibility with existing code
}

