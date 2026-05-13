package org.agenda.app;

import org.agenda.shared.config.DatabaseConnection;
import org.agenda.task.controller.TaskController;
import org.agenda.task.repository.TaskRepositoryImpl;
import org.agenda.task.service.TaskServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();

            TaskRepositoryImpl taskRepository = new TaskRepositoryImpl(connection);
            TaskServiceImpl taskService = new TaskServiceImpl(taskRepository);
            TaskController taskController = new TaskController(taskService, taskRepository, scanner);

            // NOTE DOMAIN

            // EVENT DOMAIN

            showMainMenu(scanner, taskController);

        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void showMainMenu(Scanner scanner, TaskController taskController) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== CLI AGENDA ===");
            System.out.println("1. Manage Tasks");
            System.out.println("2. Manage Notes");
            System.out.println("3. Manage Events");
            System.out.println("0. Exit");
            System.out.println("Please choose an option");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> taskController.showMenu();
                case "2" -> System.out.println("Note module under development.");
                case "3" -> System.out.println("Event module under development.");
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}