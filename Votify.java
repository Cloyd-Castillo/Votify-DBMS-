import java.sql.*;
import java.util.Scanner;

import services.AdminService;
import services.UserService;
import utils.DatabaseConnector;

public class Votify {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DatabaseConnector.initializeDatabase();
        while (true) {
            System.out.println("\n--- Welcome to Votify System ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    UserService.registerUser();
                    break;
                case 2:
                    UserService.loginUser();
                    break;
                case 3:
                    AdminService.adminLogin();
                    break;
                case 4:
                    System.out.println("Thank you for using Votify!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
}
