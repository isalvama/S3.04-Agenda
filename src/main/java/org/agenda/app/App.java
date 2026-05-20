package org.agenda.app;

import org.agenda.event.controller.EventController;
import org.agenda.event.controller.EventNotifier;
import org.agenda.event.repository.EventRepository;
import org.agenda.event.repository.EventRepositoryImpl;
import org.agenda.event.service.EventRecurringService;
import org.agenda.event.service.EventService;
import org.agenda.event.service.EventServiceImpl;
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
import java.util.concurrent.*;

public class App {

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();

            // TASK DOMAIN
            TaskRepositoryImpl taskRepository = new TaskRepositoryImpl(connection);
            TaskServiceImpl taskService = new TaskServiceImpl(taskRepository);
            TaskController taskController = new TaskController(taskService, taskRepository, scanner);

            // NOTE DOMAIN
            NoteRepositoryImpl noteRepository = new NoteRepositoryImpl(connection);
            NoteServiceImpl noteService = new NoteServiceImpl(noteRepository, taskRepository);
            NoteController noteController = new NoteController(noteService, taskService, scanner);

            // EVENT DOMAIN
            EventRepository eventRepository = new EventRepositoryImpl(connection);
            EventService eventService = new EventServiceImpl(eventRepository);
            EventController eventController = new EventController(eventService, scanner);

            EventRecurringService eventRecurringService = new EventRecurringService(eventRepository);
            eventRecurringService.processRecurringEvents();
            scheduler.scheduleAtFixedRate(eventRecurringService::processRecurringEvents, 1, 1, TimeUnit.MINUTES);

            EventNotifier eventNotifier = new EventNotifier(eventService);
            eventNotifier.notifyAllEvents();

            showMainMenu(scanner, taskController, noteController, eventController);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        } finally {
            System.out.println("Shutting down scheduler...");
            scheduler.shutdown();
            scanner.close();
        }
    }

    public static void showMainMenu(Scanner scanner, TaskController taskController, NoteController noteController, EventController eventController) {
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
                case "3" -> eventController.showMenu();
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}