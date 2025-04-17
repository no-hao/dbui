# Game Database Management System

## Database Assignment 2
*[Your Name(s) Here]*

## Overview
This project implements a Java-based database management system for a game application. It provides a user interface to manage players, characters, abilities, and locations stored in a MySQL database.

## Project Structure
The project is organized as follows:

```
src/
├── db/                      # Model classes
│   ├── Character.java       # Game character entity
│   ├── Player.java          # Player entity
│   ├── Ability.java         # Ability entity
│   ├── Location.java        # Location entity
│   ├── DatabaseManager.java # Database connection for Parts I & II
│   ├── server/              # Server-side code
│   │   └── MessageServer.java # Message server (Part III)
│   ├── client/              # Client-side code
│   │   └── MessageClient.java # Message client
│   └── ui/                  # User interface
│       ├── DatabaseUI.java  # Main UI class
│       ├── panels/          # UI panels
│       └── dialogs/         # UI dialogs
```

## Requirements
- Java JDK 11 or higher
- MySQL Server 8.0 or higher
- MySQL JDBC Driver (included in lib/ directory)

## Setup Instructions

### 1. Local Development Environment

#### Prerequisites
- JDK 11 or higher
- MySQL Server installed locally
- MySQL Connector/J (included in the `lib` folder)

#### Database Setup
1. Create a MySQL database named `gamedb`:
   ```sql
   CREATE DATABASE gamedb;
   ```

2. Update the database connection settings in `src/db/server/MessageServer.java`:
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "yourpassword";
   ```

#### Compilation and Running

1. **Compile the server and client:**
   ```bash
   # Navigate to the src directory
   cd src
   
   # Compile the server
   javac -cp "../lib/mysql-connector-j-8.0.33.jar:." db/server/MessageServer.java
   
   # Compile the client UI
   javac -cp "../lib/mysql-connector-j-8.0.33.jar:." db/ui/DatabaseUI.java
   ```

   Note: On Windows, replace the colon (`:`) with a semicolon (`;`) in the classpath.

2. **Run the server:**
   ```bash
   # From the src directory
   java -cp "../lib/mysql-connector-j-8.0.33.jar:." db.server.MessageServer
   ```

3. **Run the client:**
   ```bash
   # In a new terminal, from the src directory
   java -cp "../lib/mysql-connector-j-8.0.33.jar:." db.ui.DatabaseUI
   ```

### 2. School Server Deployment

#### Prerequisites
- SSH access to the school server
- Proper credentials for the university database

#### Database Configuration
1. Ensure you're connected to the university network or using VPN if connecting remotely

2. Update the database connection settings in `src/db/server/MessageServer.java` to use the university database:
   ```java
   private static final String DB_URL = "jdbc:mysql://db.engr.ship.edu:3306/cmsc471_27?useTimezone=true&serverTimezone=UTC";
   private static final String DB_USER = "cmsc471_27";
   private static final String DB_PASSWORD = "Password_27";
   ```

#### Deployment Steps

1. **SSH into the school server:**
   ```bash
   ssh your_username@your_school_server.edu
   ```

2. **Copy your project files to the server** (or clone from your repository if available)

3. **Compile and run the server:**
   ```bash
   # Navigate to the src directory
   cd src
   
   # Compile the server
   javac -cp "../lib/mysql-connector-j-8.0.33.jar:." db/server/MessageServer.java
   
   # Run the server
   java -cp "../lib/mysql-connector-j-8.0.33.jar:." db.server.MessageServer
   ```

4. **Run the client:**
   - If you're running the client on the same server:
     ```bash
     # In a new terminal, from the src directory
     java -cp "../lib/mysql-connector-j-8.0.33.jar:." db.ui.DatabaseUI
     ```
   - If you want to run the client on your local machine:
     1. Update the `SERVER_HOST` in `src/db/client/MessageClient.java` to your school server's address
     2. Compile and run the client locally as described in the local setup

## Usage Instructions

### Managing Players
1. Navigate to the "Players" tab
2. Use the "Add Player" button to create a new player
3. Select a player and click "Edit Selected" to modify player details
4. Select a player and click "Delete Selected" to remove a player

### Managing Characters
1. Navigate to the "Characters" tab
2. Use the "Add Character" button to create a new character
3. Select a character and click "Edit Selected" to modify character details
4. Select a character and click "Delete Selected" to remove a character

### Managing Abilities
1. Navigate to the "Abilities" tab
2. Use the provided controls to add, edit, and delete abilities

### Managing Locations (Group of 4 assignment)
1. Navigate to the "Locations" tab
2. Use the provided controls to add, edit, and delete locations

## Troubleshooting

### Connection Issues
- Ensure the server is running before starting the client
- Check if the specified port (4446) is available and not blocked by a firewall
- Verify database connection settings match your environment

### Database Errors
- Check that your MySQL server is running
- Verify database credentials are correct
- Ensure you have the necessary permissions to create and modify tables

### Client-Server Communication
- If using "localhost" doesn't work, try "127.0.0.1" instead
- Ensure the proper MySQL connector version is being used (included in the lib folder)

## Architecture

This application follows a three-tier architecture:

1. **Presentation Tier (UI)**: The Java Swing interface in the `db.ui` package
2. **Application Tier (Server)**: The MessageServer that processes client requests
3. **Data Tier**: The MySQL database that stores all game data

Communication between tiers:
- The UI sends requests to the server via the MessageClient
- The server processes these requests and interacts with the database
- The server returns results to the client for display in the UI

This ensures a clean separation of concerns and allows for flexible deployment options.

## Database Schema
The application implements the following database schema:

```sql
CREATE TABLE PERSON (
    loginId VARCHAR(12) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    dateCreated DATE NOT NULL,
    PRIMARY KEY (loginId),
    UNIQUE(email)
);

CREATE TABLE MANAGER (
    loginId VARCHAR(12) NOT NULL,
    PRIMARY KEY (loginId),
    FOREIGN KEY (loginId) REFERENCES PERSON (loginId)
);

CREATE TABLE MODERATOR (
    loginId VARCHAR(12) NOT NULL,
    isSilenced BOOLEAN NOT NULL,
    isBlocked BOOLEAN NOT NULL,
    worksWith VARCHAR(12),
    PRIMARY KEY (loginId),
    FOREIGN KEY (loginId) REFERENCES PERSON (loginId),
    FOREIGN KEY (worksWith) REFERENCES MANAGER (loginId)
);

CREATE TABLE PLAYER (
    loginId VARCHAR(12) NOT NULL,
    isSilenced BOOLEAN NOT NULL,
    isBlocked BOOLEAN NOT NULL,
    watchedBy VARCHAR(12),
    PRIMARY KEY (loginId),
    FOREIGN KEY (loginId) REFERENCES PERSON (loginId),
    FOREIGN KEY (watchedBy) REFERENCES PERSON (loginId)
);

-- Additional tables omitted for brevity (see DatabaseManager.java for complete schema)
```

## Usage
1. Launch the UI application
2. Use the tabs to navigate between different management screens:
   - Players: Add, edit, and delete player accounts
   - Characters: Create game characters associated with players
   - Abilities: Manage character abilities
   - Locations: Define game world locations

## Troubleshooting
1. **Connection Issues:**
   - Check that your MySQL server is running
   - Verify the connection parameters (URL, username, password)
   - For school server, ensure VPN is connected if required

2. **Database Table Creation:**
   - If you encounter foreign key constraint errors, you may need to drop existing tables:
     ```sql
     mysql -u root -p
     USE gamedb;
     SET FOREIGN_KEY_CHECKS = 0;
     DROP TABLE IF EXISTS PERSON, PLAYER, MANAGER, MODERATOR, LOCATION, GAMECHARACTER;
     SET FOREIGN_KEY_CHECKS = 1;
     exit
     ```
   - Then run DatabaseManager again

3. **UI Components Not Displaying Correctly:**
   - Ensure MessageServer is running
   - Check console for errors related to database queries or table/column names

## Notes for Instructors
- The application fully implements the schema exactly as requested
- Both local development mode and school server mode are supported
- All database operations use prepared statements to prevent SQL injection
- The server implements stored procedures for more complex operations 