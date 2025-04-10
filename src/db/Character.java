package db;

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

    // Add getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getPlayerEmail() { return playerEmail; }
    public int getMaxHp() { return maxHp; }
    public int getStrength() { return strength; }
    public int getStamina() { return stamina; }
}

