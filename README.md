# ✨ Votify: Online Voting System ✨

## *I. Project Overview*

Votify is a console-based Java application designed to provide a secure, efficient, and user-friendly online voting system. It allows voters to participate in polls, view results, and ensures proper data management and privacy. The system connects to a relational database, which stores user accounts, polls, and vote data, offering scalability and secure data management.

## *II. Key Features*

1. *🔐 User Account Management:*
    - Secure user registration and login functionality.
    - Stores user credentials (username and password) in a MySQL database.
    - Ensures data privacy by protecting users' personal data.

2. *📊 Poll Management:*
    - Create polls with different voting options.
    - View the list of active polls and their results.
    - End polls and finalize voting once the desired period is over.

3. *🗳️ Voting Functionality:*
    - Voters can cast their votes for various polls.
    - Tracks the votes and provides a summary of results after voting completion.
    - Includes validation checks to ensure each user votes only once.

4. *📁 Database Integration:*
    - All user accounts, polls, and votes are stored in a MySQL database.
    - Supports efficient data management, persistence, and scalability through relational database architecture.

## *III. Integration of DBMS*

Votify integrates a *Relational Database Management System (RDBMS)* using MySQL to store and manage users' data, polls, and votes. The application performs operations such as inserting, updating, and retrieving poll and vote data.

### *Database Schema:*

1. *Users Table:*
    - Stores user data (username and password).
    - Columns: id, username, password, is_active.

2. *Polls Table:*
    - Stores poll data (title, description, start date, end date, created_by).
    - Columns: id, title, description, start_date, end_date, created_by.

3. *Poll Choices Table:*
    - Stores choices/options for each poll.
    - Columns: id, poll_id, choice.

4. *Votes Table:*
    - Stores each vote for a poll choice.
    - Columns: id, user_id, poll_choice_id.

5. *User Votes Table:*
    - Tracks which user has voted in which poll.
    - Columns: user_id, poll_id.

### *Relationships:*
- A **user** can create multiple **polls**.
- A **poll** can have multiple **poll choices**.
- A **user** can vote in a **poll** but only once.
- A **vote** is linked to a **poll choice**, recording which option a **user** selected.

The program uses JDBC (Java Database Connectivity) to interact with the database, enabling secure login, poll management, and voting functionality.

## *IV. Instructions on How to Run the Program*

### *📋 Prerequisites:*
1. *Code Editor or IDE:* e.g., IntelliJ IDEA, Eclipse, or Visual Studio Code.
2. *Java Development Kit (JDK):* Ensure JDK 8 or later is installed on your system.
3. *MySQL Database:* A MySQL instance running on your machine or accessible remotely.

### *🚀 Steps to Run the Program:*

1. *📥 Download and Place Files:*
    - Save all .java files into a folder called votify-system.

2. *🗂️ Create and Place Supporting Files:*
    - *Database Setup:*
        - Run the following SQL script to create the database and tables:

```sql
CREATE DATABASE votify;

USE votify;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE polls (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE poll_choices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT NOT NULL,
    choice VARCHAR(255) NOT NULL,
    FOREIGN KEY (poll_id) REFERENCES polls(id)
);

CREATE TABLE votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    poll_choice_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (poll_choice_id) REFERENCES poll_choices(id)
);

CREATE TABLE user_votes (
    user_id INT NOT NULL,
    poll_id INT NOT NULL,
    PRIMARY KEY (user_id, poll_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (poll_id) REFERENCES polls(id)
);
```
3. *🔨 Compile the Program:*
    - Open a terminal or command prompt.
    - Navigate to the flashcard-system folder.
    - Compile the program using:
 ```   
javac *.java
```
4. *▶️ Run the Program:*
    - After successful compilation, run the program by executing:
 ```   
java Main
```
### *📁 Folder Structure:*
```
flashcard-system/
├── Main.java
├── UserAccount.java
├── FlashcardManager.java
├── DatabaseConnection.java
└── (MySQL Database)
```
5. *🧩 Connector:*
- Make sure that you have the MySQL Connector JAR file in your Java project's reference libraries.
