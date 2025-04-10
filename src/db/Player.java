package db;

import java.time.LocalDate;

public class Player {
    private String loginId;
    private String email;
    private String passwordHash;
    private LocalDate dateCreated;
    private boolean isSilenced;
    private boolean isBlocked;
    private String watchedBy;

    public Player(String loginId, String email, String password) {
        this.loginId = loginId;
        this.email = email;
        this.passwordHash = password; // In a real app, this would be hashed
        this.dateCreated = LocalDate.now();
        this.isSilenced = false;
        this.isBlocked = false;
        this.watchedBy = null;
    }

    // Getters
    public String getLoginId() { return loginId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDate getDateCreated() { return dateCreated; }
    public boolean isSilenced() { return isSilenced; }
    public boolean isBlocked() { return isBlocked; }
    public String getWatchedBy() { return watchedBy; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String password) { this.passwordHash = password; }
    public void setSilenced(boolean silenced) { this.isSilenced = silenced; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }
    public void setWatchedBy(String watchedBy) { this.watchedBy = watchedBy; }
}

