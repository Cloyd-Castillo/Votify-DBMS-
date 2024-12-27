package services;

import utils.DatabaseConnector;
import java.sql.*;
import java.util.Scanner;

public class AdminService {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";

    public static void adminLogin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();

        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        try {
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                System.out.println("Admin login successful! Welcome, Admin.");
                adminMenu();
            } else {
                System.out.println("Invalid admin credentials. Try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during admin login: " + e.getMessage());
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Create a Poll");
            System.out.println("2. View Poll Results");
            System.out.println("3. End a Poll");
            System.out.println("4. Account Deactivation");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
    
            int choice = -1;
            while (choice == -1) {
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice < 1 || choice > 5) {
                        System.out.println("Invalid option! Please try again.");
                        choice = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid number.");
                }
            }
    
            try {
                switch (choice) {
                    case 1:
                        createPoll();
                        break;
                    case 2:
                        viewPollResults();
                        break;
                    case 3:
                        endPoll();
                        break;
                    case 4:
                        accountDeactivations();
                        break;
                    case 5:
                        return;
                }
            } catch (Exception e) {
                System.out.println("Error during admin menu operation: " + e.getMessage());
            }
        }
    }
    

    private static void createPoll() {
        System.out.print("Enter poll title: ");
        String title = scanner.nextLine();

        System.out.print("Enter duration of poll (in days): ");
        int duration = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO polls (title, start_date, end_date) VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL ? DAY))",
                Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, title);
            stmt.setInt(2, duration);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int pollId = -1;
            if (rs.next()) {
                pollId = rs.getInt(1);
            }

            System.out.println("Enter poll options (type 'done' to finish): ");
            while (true) {
                System.out.print("Option: ");
                String option = scanner.nextLine();
                if (option.equalsIgnoreCase("done")) {
                    break;
                }

                PreparedStatement optionStmt = conn.prepareStatement(
                    "INSERT INTO poll_choices (poll_id, choice) VALUES (?, ?)"
                );
                optionStmt.setInt(1, pollId);
                optionStmt.setString(2, option);
                optionStmt.executeUpdate();

                // Add one default vote for each option
                PreparedStatement defaultVoteStmt = conn.prepareStatement(
                    "INSERT INTO votes (poll_id, choice) VALUES (?, ?)"
                );
                defaultVoteStmt.setInt(1, pollId);
                defaultVoteStmt.setString(2, option);
                defaultVoteStmt.executeUpdate();
            }
            System.out.println("Poll created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating poll: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during poll creation: " + e.getMessage());
        }
    }
    

    private static void viewPollResults() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, title FROM polls");
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
                "SELECT choice, COUNT(*) AS votes FROM votes WHERE poll_id = ? GROUP BY choice"
            );
            resultStmt.setInt(1, pollId);
            ResultSet resultRs = resultStmt.executeQuery();

            System.out.println("\n--- Poll Results ---");
            while (resultRs.next()) {
                System.out.println(resultRs.getString("choice") + ": " + resultRs.getInt("votes") + " votes");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing results: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while viewing poll results: " + e.getMessage());
        }
    }

    private static void endPoll() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, title FROM polls WHERE end_date > NOW()");
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Active Polls ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("title"));
            }

            System.out.print("Enter the poll ID to end (or 0 to cancel): ");
            int pollId = scanner.nextInt();
            scanner.nextLine();

            if (pollId == 0) return;

            PreparedStatement endStmt = conn.prepareStatement("UPDATE polls SET end_date = NOW() WHERE id = ?");
            endStmt.setInt(1, pollId);
            endStmt.executeUpdate();

            System.out.println("Poll ended successfully!");
            viewPollResults();
        } catch (SQLException e) {
            System.out.println("Error ending poll: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while ending the poll: " + e.getMessage());
        }
    }

    private static void accountDeactivations() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, username FROM users WHERE is_active = FALSE");
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n--- Account Deactivation Process ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("username"));
            }
            System.out.print("Enter the user ID to approve deactivation (or 0 to cancel): ");
            int userId = scanner.nextInt();
            scanner.nextLine();
            if (userId == 0) return;
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            System.out.println("Deactivation approved. User removed from the database.");
        } catch (SQLException e) {
            System.out.println("Error deactivating account: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during account deactivation: " + e.getMessage());
        }
    }
}