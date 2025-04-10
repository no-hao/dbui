/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Player Model - Represents a player entity
 * 
 * @author [YOUR NAME HERE]
 */
package db;

import java.time.LocalDate;

/**
 * Represents a player in the database.
 * Maps to the PLAYER and PERSON tables.
 */
public class Player {
    // PERSON table fields
    private String loginId;         // Primary key
    private String email;
    private String password;
    private LocalDate dateCreated;
    
    // PLAYER table fields
    private boolean isSilenced;
    private boolean isBlocked;
    private String watchedBy;       // Foreign key to PERSON.loginId

    /**
     * Creates a new player with minimal required information.
     * 
     * @param loginId Player's login ID (primary key)
     * @param email Player's email address
     * @param password Player's password (in a real app, this would be hashed)
     */
    public Player(String loginId, String email, String password) {
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.dateCreated = LocalDate.now();
        this.isSilenced = false;
        this.isBlocked = false;
        this.watchedBy = null;
    }

    /**
     * Creates a new player with complete information.
     * 
     * @param loginId Player's login ID (primary key)
     * @param email Player's email address
     * @param password Player's password (in a real app, this would be hashed)
     * @param dateCreated Date the player account was created
     * @param isSilenced Whether the player is silenced
     * @param isBlocked Whether the player is blocked
     * @param watchedBy Login ID of the person watching this player
     */
    public Player(String loginId, String email, String password, LocalDate dateCreated,
                boolean isSilenced, boolean isBlocked, String watchedBy) {
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.dateCreated = dateCreated;
        this.isSilenced = isSilenced;
        this.isBlocked = isBlocked;
        this.watchedBy = watchedBy;
    }

    // Getters
    public String getLoginId() { return loginId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public LocalDate getDateCreated() { return dateCreated; }
    public boolean isSilenced() { return isSilenced; }
    public boolean isBlocked() { return isBlocked; }
    public String getWatchedBy() { return watchedBy; }
    
    // Setters
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }
    public void setSilenced(boolean silenced) { this.isSilenced = silenced; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }
    public void setWatchedBy(String watchedBy) { this.watchedBy = watchedBy; }
    
    // For compatibility with existing code
    public String getPasswordHash() { return password; }
    public void setPasswordHash(String password) { this.password = password; }
}

