package org.agenda.app;

import org.agenda.note.controller.NoteController;
import org.agenda.note.repository.NoteRepositoryImpl;
import org.agenda.note.service.NoteServiceImpl;
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

            // TASK DOMAIN
            TaskRepositoryImpl taskRepository = new TaskRepositoryImpl(connection);
            TaskServiceImpl taskService = new TaskServiceImpl(taskRepository);
            TaskController taskController = new TaskController(taskService, taskRepository, scanner);

            // NOTE DOMAIN
            NoteRepositoryImpl noteRepository = new NoteRepositoryImpl(connection);
            NoteServiceImpl noteService = new NoteServiceImpl(noteRepository, taskRepository);
            NoteController noteController = new NoteController(noteService, taskService);

            // EVENT DOMAIN

            showMainMenu(scanner, taskController, noteController);

        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void showMainMenu(Scanner scanner, TaskController taskController, NoteController noteController) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== CLI AGENDA ===");
            System.out.println("1. Manage Tasks");
            System.out.println("2. Manage Notes");
            System.out.println("3. Manage Events");
            System.out.println("0. Exit");
            System.out.print("Please choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> taskController.showMenu();
                case "2" -> noteController.showMenu();
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