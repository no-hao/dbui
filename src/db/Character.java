/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Character Model - Represents a game character entity
 * 
 * @author [YOUR NAME HERE]
 */
package db;

/**
 * Represents a game character in the database.
 * Maps to the GAMECHARACTER table.
 */
public class Character {
    private String name;           // Primary key
    private String playerId;       // Foreign key to PLAYER.loginId
    private int maxPoints;
    private int currentPoints;
    private int stamina;
    private int strength;
    private byte[] profilePicture; // MEDIUMBLOB
    private String locationId;     // Foreign key to LOCATION.lId

    /**
     * Creates a new character with minimal required information.
     * 
     * @param name Character's name (primary key)
     * @param playerId Player's login ID (foreign key)
     * @param maxPoints Maximum hit points
     * @param stamina Stamina value
     * @param strength Strength value
     */
    public Character(String name, String playerId, int maxPoints, int stamina, int strength) {
        this.name = name;
        this.playerId = playerId;
        this.maxPoints = maxPoints;
        this.currentPoints = maxPoints; // Start with full points
        this.stamina = stamina;
        this.strength = strength;
        this.profilePicture = null;
        this.locationId = "default"; // Default location
    }

    /**
     * Creates a new character with complete information.
     * 
     * @param name Character's name (primary key)
     * @param playerId Player's login ID (foreign key)
     * @param maxPoints Maximum hit points
     * @param currentPoints Current hit points
     * @param stamina Stamina value
     * @param strength Strength value
     * @param locationId Location ID (foreign key)
     */
    public Character(String name, String playerId, int maxPoints, int currentPoints, int stamina, int strength, String locationId) {
        this.name = name;
        this.playerId = playerId;
        this.maxPoints = maxPoints;
        this.currentPoints = currentPoints;
        this.stamina = stamina;
        this.strength = strength;
        this.profilePicture = null;
        this.locationId = locationId;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public int getMaxPoints() { return maxPoints; }
    public void setMaxPoints(int maxPoints) { this.maxPoints = maxPoints; }

    public int getCurrentPoints() { return currentPoints; }
    public void setCurrentPoints(int currentPoints) { this.currentPoints = currentPoints; }

    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public byte[] getProfilePicture() { return profilePicture; }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }

    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }

    // Alias for maxPoints to match UI
    public int getMaxHp() { return maxPoints; }
    public void setMaxHp(int maxHp) { this.maxPoints = maxHp; }
}

