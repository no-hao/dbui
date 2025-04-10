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
    // Primary key
    private String name;
    
    // Foreign keys
    private String playerId;
    private String locationId;
    
    // Character attributes
    private int maxPoints;
    private int currentPoints;
    private int stamina;
    private int strength;
    private byte[] profilePicture;

    /**
     * Creates a new character with minimal required information.
     * 
     * @param name Character name (primary key)
     * @param playerId Player's login ID who owns this character
     * @param maxPoints Maximum hit points
     * @param currentPoints Current hit points
     * @param stamina Character's stamina value
     * @param strength Character's strength value
     */
    public Character(String name, String playerId, int maxPoints, int currentPoints, int stamina, int strength) {
        this.name = name;
        this.playerId = playerId;
        this.maxPoints = maxPoints;
        this.currentPoints = currentPoints;
        this.stamina = stamina;
        this.strength = strength;
        this.locationId = "default"; // Default location
    }
    
    /**
     * Creates a new character with complete information.
     * 
     * @param name Character name (primary key)
     * @param playerId Player's login ID who owns this character
     * @param maxPoints Maximum hit points
     * @param currentPoints Current hit points
     * @param stamina Character's stamina value
     * @param strength Character's strength value
     * @param locationId Location ID where the character is located
     * @param profilePicture Character's profile picture as byte array
     */
    public Character(String name, String playerId, int maxPoints, int currentPoints, 
                   int stamina, int strength, String locationId, byte[] profilePicture) {
        this.name = name;
        this.playerId = playerId;
        this.maxPoints = maxPoints;
        this.currentPoints = currentPoints;
        this.stamina = stamina;
        this.strength = strength;
        this.locationId = locationId;
        this.profilePicture = profilePicture;
    }
    
    // For backward compatibility with existing code
    public Character(int id, String name, String playerEmail, int maxHp, int strength, int stamina) {
        this.name = name;
        this.playerId = "user" + id; // Convert ID to a player ID format
        this.maxPoints = maxHp;
        this.currentPoints = maxHp; // Default to full health
        this.stamina = stamina;
        this.strength = strength;
        this.locationId = "default"; // Default location
    }

    // Getters
    public String getName() { return name; }
    public String getPlayerId() { return playerId; }
    public int getMaxPoints() { return maxPoints; }
    public int getCurrentPoints() { return currentPoints; }
    public int getStamina() { return stamina; }
    public int getStrength() { return strength; }
    public String getLocationId() { return locationId; }
    public byte[] getProfilePicture() { return profilePicture; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public void setMaxPoints(int maxPoints) { this.maxPoints = maxPoints; }
    public void setCurrentPoints(int currentPoints) { this.currentPoints = currentPoints; }
    public void setStamina(int stamina) { this.stamina = stamina; }
    public void setStrength(int strength) { this.strength = strength; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }
    
    // Compatibility methods for existing code
    public int getId() { return playerId != null ? playerId.hashCode() : 0; }
    public void setId(int id) { this.playerId = "user" + id; }
    public String getPlayerEmail() { return playerId + "@example.com"; } // Mock email based on playerId
    public int getMaxHp() { return maxPoints; }
}

