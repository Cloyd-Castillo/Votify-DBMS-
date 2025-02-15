package services;

import utils.DatabaseConnector;

import java.sql.*;
import java.util.Scanner;

public class UserService {
    private static final Scanner scanner = new Scanner(System.in);
    private int userId;

    public UserService(int userId) {
        this.userId = userId;
    }

    public static void registerUser() {
        String username = "";
        String password = "";

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();

            if (username.length() < 4) {
                System.out.println("Username must be at least 4 characters long. Please try again.");
                continue;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username already exists. Please choose another username.");
                } else {
                    break; 
                }
            } catch (SQLException e) {
                System.out.println("Error checking username: " + e.getMessage());
                e.printStackTrace();
            }
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();

            if (password.length() < 8) {
                System.out.println("Password must be at least 8 characters long. Please try again.");
                continue;
            }
            break; 
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("Registration successful!");
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loginUser() {
        String username = "";
        String password = "";

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();

            if (username.length() < 4) {
                System.out.println("Username must be at least 4 characters long. Please try again.");
                continue;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Username does not exist. Please try again.");
                } else {
                    break; 
                }
            } catch (SQLException e) {
                System.out.println("Error checking username: " + e.getMessage());
                e.printStackTrace();
            }
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();

            if (password.length() < 8) {
                System.out.println("Password must be at least 8 characters long. Please try again.");
                continue;
            }
            break; 
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful! Welcome, " + username + "!");

                UserService userService = new UserService(rs.getInt("id"));
                userService.userMenu();
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void userMenu() {
        while (true) {
            try {
                System.out.println("\n--- User Menu ---");
                System.out.println("1. Vote in a Poll");
                System.out.println("2. View Poll Results");
                System.out.println("3. Change Password");
                System.out.println("4. Back to Main Menu");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        voteInPoll();
                        break;
                    case 2:
                        viewPollResults();
                        break;
                    case 3:
                        changePassword();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error in user menu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void changePassword() {
        System.out.print("Enter your current password: ");
        String currentPassword = scanner.nextLine();

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE id = ? AND password = ?");
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, currentPassword);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Current password is incorrect.");
                return;
            }

            System.out.print("Enter your new password: ");
            String newPassword = scanner.nextLine();

            if (newPassword.length() < 8) {
                System.out.println("New password must be at least 8 characters long.");
                return;
            }

            PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?");
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, userId);
            updateStmt.executeUpdate();

            System.out.println("Password updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void voteInPoll() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, title, DATE_FORMAT(end_date, '%b %d, %Y') AS formatted_end_date " +
                "FROM polls WHERE end_date > NOW()");
            ResultSet rs = stmt.executeQuery();
    
            System.out.println("\n--- Active Polls ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("title") +
                                   " (Ends on " + rs.getString("formatted_end_date") + ")");
            }
    
            System.out.print("Enter the poll ID to vote (or 0 to cancel): ");
            int pollId = scanner.nextInt();
            scanner.nextLine();
    
            if (pollId == 0) return;
    
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT * FROM user_votes WHERE user_id = ? AND poll_id = ?");
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, pollId);
            ResultSet checkRs = checkStmt.executeQuery();
    
            if (checkRs.next()) {
                System.out.println("You have already voted in this poll.");
                return;
            }
    
            PreparedStatement optionsStmt = conn.prepareStatement(
                "SELECT DISTINCT choice FROM votes WHERE poll_id = ?");
            optionsStmt.setInt(1, pollId);
            ResultSet optionsRs = optionsStmt.executeQuery();
    
            System.out.println("\n--- Poll Options ---");
            while (optionsRs.next()) {
                System.out.println("- " + optionsRs.getString("choice"));
            }
    
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
    
            PreparedStatement voteStmt = conn.prepareStatement(
                "INSERT INTO user_votes (user_id, poll_id) VALUES (?, ?)");
            voteStmt.setInt(1, userId);
            voteStmt.setInt(2, pollId);
            voteStmt.executeUpdate();
    
            PreparedStatement updateVotesStmt = conn.prepareStatement(
                "INSERT INTO votes (poll_id, choice) VALUES (?, ?)");
            updateVotesStmt.setInt(1, pollId);
            updateVotesStmt.setString(2, choice);
            updateVotesStmt.executeUpdate();
    
            System.out.println("Your vote has been recorded successfully!");
        } catch (SQLException e) {
            System.out.println("Error while voting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewPollResults() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, title FROM polls");
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Available Polls ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("title"));
            }

            System.out.print("Enter the poll ID to view results (or 0 to cancel): ");
            int pollId = scanner.nextInt();
            scanner.nextLine();

            if (pollId == 0) return;

            PreparedStatement resultStmt = conn.prepareStatement(
                "SELECT choice, COUNT(*) AS votes FROM votes WHERE poll_id = ? GROUP BY choice");
            resultStmt.setInt(1, pollId);
            ResultSet resultRs = resultStmt.executeQuery();

            System.out.println("\n--- Poll Results ---");
            while (resultRs.next()) {
                System.out.println(resultRs.getString("choice") + ": " + resultRs.getInt("votes") + " votes");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing poll results: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
