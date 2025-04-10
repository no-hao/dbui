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
- MySQL JDBC Driver (included or add to classpath)

## Setup Instructions

### Database Setup
1. Install MySQL Server if not already installed
2. Create a new database named 'gamedb'
```sql
CREATE DATABASE gamedb;
```
3. Create a user with appropriate privileges or use root user
4. Update the database connection parameters in:
   - `src/db/DatabaseManager.java`
   - `src/db/server/MessageServer.java`

### Project Setup
1. Compile the project:
```
javac -d bin src/db/*.java src/db/ui/DatabaseUI.java src/db/ui/panels/*.java src/db/ui/dialogs/*.java
```

2. Create database tables (Part I):
```
java -cp bin:mysql-connector-java.jar db.DatabaseManager
```

3. Start the message server (Part III):
```
java -cp bin:mysql-connector-java.jar db.server.MessageServer
```

4. Start the UI application (Part IV):
```
java -cp bin:mysql-connector-java.jar db.ui.DatabaseUI
```

## Assignment Components

### Part I - Database Creation
The `DatabaseManager.java` class contains methods to create all required database tables.

### Part II - Database Population
The `DatabaseManager.java` class also includes methods to populate the database with initial data.

### Part III - Message Server
The `MessageServer.java` and `MessageClient.java` classes implement the client-server architecture required for the assignment. The server uses PreparedStatements and includes a Stored Procedure.

### Part IV - User Interface
The UI components in the `db.ui` package implement the four required displays:
1. Character management
2. Player management
3. Ability management
4. Location management

## Usage
1. Launch the UI application
2. Use the tabs to navigate between different management screens
3. Each screen provides functionality to view, add, edit, and delete entries 