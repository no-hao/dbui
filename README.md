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

### Database Configuration
You can configure the application to use either your local MySQL server or a school server.

#### Option 1: Local MySQL Server (Recommended for Development)
1. Install MySQL Server if not already installed
   ```
   # For MacOS using Homebrew
   brew install mysql
   brew services start mysql
   ```

2. Create a new database named 'gamedb'
   ```sql
   mysql -u root -p
   CREATE DATABASE gamedb;
   exit
   ```

3. Update the database connection parameters in:
   - `src/db/DatabaseManager.java`
   - `src/db/server/MessageServer.java`

   Use these settings for local MySQL:
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
   private static final String DB_USER = "root"; 
   private static final String DB_PASSWORD = "your_password"; // Replace with your MySQL root password
   ```

#### Option 2: School MySQL Server
1. Comment out the local MySQL connection parameters and uncomment the school server connection in:
   - `src/db/DatabaseManager.java`
   - `src/db/server/MessageServer.java`

   ```java
   // School server connection
   private static final String DB_URL = "jdbc:mysql://school_server:3306/gamedb";
   private static final String DB_USER = "your_school_username"; 
   private static final String DB_PASSWORD = "your_school_password"; 
   
   // Local MySQL connection (commented out)
   // private static final String DB_URL = "jdbc:mysql://localhost:3306/gamedb";
   // private static final String DB_USER = "root"; 
   // private static final String DB_PASSWORD = "your_password";
   ```

### Project Setup and Compilation
1. Ensure MySQL JDBC driver is in the lib/ folder
   - The project uses mysql-connector-j-8.0.33.jar

2. Compile the project:
   ```
   mkdir -p bin
   javac -d bin -cp lib/mysql-connector-j-8.0.33.jar $(find src -name "*.java")
   ```

### Database Initialization
1. Create and populate database tables:
   ```
   java -cp bin:lib/mysql-connector-j-8.0.33.jar db.DatabaseManager
   ```
   
   This script:
   - Creates all required tables with appropriate constraints
   - Adds sample data for testing

### Running the Application
1. Start the message server (must be running for UI to work properly):
   ```
   java -cp bin:lib/mysql-connector-j-8.0.33.jar db.server.MessageServer
   ```

2. In a separate terminal, launch the UI application:
   ```
   java -cp bin:lib/mysql-connector-j-8.0.33.jar db.ui.DatabaseUI
   ```

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