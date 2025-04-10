/**
 * Database Assignment 2
 * Game Database Management System
 * 
 * Location Model - Represents a location entity
 * 
 * @author [YOUR NAME HERE]
 */
package db;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a location in the database.
 * Maps to the LOCATION and EXITSTO tables.
 */
public class Location {
    // LOCATION table fields
    private String lId;           // Primary key in the database
    private int size;
    private String type;
    
    // Additional fields for UI
    private String name;
    private String description;
    
    // List of locations this location exits to
    private List<Location> exits;

    /**
     * Creates a new location with minimal required information.
     * 
     * @param lId Location ID (primary key)
     * @param name Location name for display purposes
     * @param description Location description
     * @param type Location type (e.g., TOWN, DUNGEON, FOREST)
     */
    public Location(String lId, String name, String description, String type) {
        this.lId = lId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.size = 1; // Default size
        this.exits = new ArrayList<>();
    }
    
    /**
     * Creates a new location with complete information.
     * 
     * @param lId Location ID (primary key)
     * @param name Location name for display purposes
     * @param description Location description
     * @param type Location type (e.g., TOWN, DUNGEON, FOREST)
     * @param size Location size
     * @param exits List of locations this location exits to
     */
    public Location(String lId, String name, String description, String type, int size, List<Location> exits) {
        this.lId = lId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.size = size;
        this.exits = exits != null ? exits : new ArrayList<>();
    }
    
    // For backward compatibility with existing code that uses numeric IDs
    public Location(int id, String name, String description, String type) {
        this("L" + id, name, description, type);
    }
    
    public Location(int id, String name, String description, String type, int size, List<Location> exits) {
        this("L" + id, name, description, type, size, exits);
    }

    // Getters
    public String getLId() { return lId; }
    public int getSize() { return size; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    // Get a copy of the exits list to prevent direct modification
    public List<Location> getExits() { return new ArrayList<>(exits); }
    
    // Get exit location names as a comma-separated string
    public String getExitsAsString() {
        if (exits.isEmpty()) {
            return "None";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < exits.size(); i++) {
            sb.append(exits.get(i).getName());
            if (i < exits.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    // Setters
    public void setLId(String lId) { this.lId = lId; }
    public void setSize(int size) { this.size = size; }
    public void setType(String type) { this.type = type; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    
    // Manage exits
    public void addExit(Location location) {
        if (location != null && !exits.contains(location)) {
            exits.add(location);
        }
    }
    
    public void removeExit(Location location) {
        exits.remove(location);
    }
    
    public boolean hasExit(Location location) {
        return exits.contains(location);
    }
    
    public boolean hasExitTo(String locationId) {
        return exits.stream().anyMatch(l -> l.getLId().equals(locationId));
    }
    
    // For backward compatibility with existing code
    public int getId() { 
        if (lId.startsWith("L")) {
            try {
                return Integer.parseInt(lId.substring(1));
            } catch (NumberFormatException e) {
                return lId.hashCode();
            }
        }
        return lId.hashCode();
    }
    
    public void setId(int id) { this.lId = "L" + id; }
    
    public boolean hasExitTo(int locationId) {
        String lid = "L" + locationId;
        return exits.stream().anyMatch(l -> l.getLId().equals(lid));
    }
}

