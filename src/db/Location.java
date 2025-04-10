package db;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for game locations
 */
public class Location {
    private int id;
    private String name;
    private String description;
    private String type;
    private int size;
    private List<Location> exits;

    /**
     * Create a new location
     */
    public Location(int id, String name, String description, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.size = 1; // Default size
        this.exits = new ArrayList<>();
    }
    
    /**
     * Create a new location with all parameters
     */
    public Location(int id, String name, String description, String type, int size, List<Location> exits) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.size = size;
        this.exits = exits != null ? exits : new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getSize() { return size; }
    
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
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setSize(int size) { this.size = size; }
    
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
    
    public boolean hasExitTo(int locationId) {
        return exits.stream().anyMatch(l -> l.getId() == locationId);
    }
}

