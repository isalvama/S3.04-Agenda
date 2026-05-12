package org.agenda.task.controller;

import org.agenda.task.dto.TaskRequest;
import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Priority;
import org.agenda.task.model.Status;
import org.agenda.task.model.Task;
import org.agenda.task.repository.TaskRepository;
import org.agenda.task.service.TaskService;
import org.agenda.task.service.strategy.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaskController {

    private final TaskService taskService;
    private final Scanner scanner;
    private final Map<String, Runnable> menuActions;
    private final TaskStrategy listAllStrategy;
    private final TaskStrategy listPendingStrategy;
    private final TaskStrategy listCompletedStrategy;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TaskController(TaskService taskService, TaskRepository repository, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
        this.listAllStrategy = new ListAllStrategy(repository);
        this.listPendingStrategy = new ListPendingStrategy(repository);
        this.listCompletedStrategy = new ListCompletedStrategy(repository);

        this.menuActions = new HashMap<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menuActions.put("1", this::createTask);
        menuActions.put("2", this::listAllTasks);
        menuActions.put("3", this::listPendingTasks);
        menuActions.put("4", this::listCompletedTasks);
        menuActions.put("5", this::markTaskAsDone);
        menuActions.put("6", this::updateTask);
        menuActions.put("7", this::deleteTask);
        menuActions.put("8", this::findTaskById);
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            if ("0".equals(choice)) {
                System.out.println("Returning to main menu...");
            } else {
                menuActions.getOrDefault(choice,
                        () -> System.out.println("Invalid option. Please try again.")).run();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- TASK MANAGEMENT ---");
        System.out.println("1. Create a task");
        System.out.println("2. List all tasks");
        System.out.println("3. List pending tasks");
        System.out.println("4. List completed tasks");
        System.out.println("5. Mark task as done");
        System.out.println("6. Update a task");
        System.out.println("7. Delete a task");
        System.out.println("8. Find a task by ID");
        System.out.println("0. Back to main menu");
        System.out.println("Select an option:");
    }

    private void createTask() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        System.out.print("Priority (HIGH / MEDIUM / LOW or leave empty): ");
        Priority priority = parsePriority(scanner.nextLine().trim(), null);

        System.out.print("Expiration Date (dd/MM/yyyy HH:mm or leave empty for +7 days): ");
        String dateInput = scanner.nextLine().trim();
        LocalDateTime expirationDate = dateInput.isBlank()
                ? LocalDateTime.now().plusDays(7)
                : parseDate(dateInput);

        if (expirationDate == null) return;

        TaskRequest request = new TaskRequest(title, body, null, priority, expirationDate, null);

        try {
            TaskResponse response = taskService.create(request);
            System.out.println("Task created with ID: " + response.id());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAllTasks() {
        System.out.println("\n--- All Tasks ---");
        displayTasks(taskService.listTasks(listAllStrategy), "No tasks found.");
    }

    private void listPendingTasks() {
        System.out.println("\n--- Pending Tasks ---");
        displayTasks(taskService.listTasks(listPendingStrategy), "No pending tasks.");
    }

    private void listCompletedTasks() {
        System.out.println("\n--- Completed Tasks ---");
        displayTasks(taskService.listTasks(listCompletedStrategy), "No completed tasks");
    }

    private void markTaskAsDone() {
        System.out.print("Enter Task ID to mark as done: ");
        Long id = readLongInput();
        if (id == null) return;

        try {
            TaskResponse response = taskService.markAsDone(id);
            System.out.println("Task #" + response.id() + " marked as DONE.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateTask() {
        System.out.print("Enter Task ID to update: ");
        Long id = readLongInput();
        if (id == null) return;

        try {
            TaskResponse current = taskService.getById(id);
            System.out.println("Updating task: " + current.title());

            System.out.print("New title (empty to keep): ");
            String title = scanner.nextLine().trim();
            if (title.isBlank()) title = current.title();

            System.out.print("New body (empty to keep): ");
            String body = scanner.nextLine().trim();
            if (body.isBlank()) body = current.body();

            System.out.print("New status (PENDING / IN PROGRESS / DONE or empty to keep): ");
            String statusInput = scanner.nextLine().trim();
            Status status = statusInput.isBlank()
                    ? current.status() : Status.fromSqlValue(statusInput);

            System.out.print("New priority (HIGH / MEDIUM / LOW or empty to keep): ");
            Priority priority = parsePriority(scanner.nextLine().trim(),
                    current.priority());

            System.out.print("New expiration date (dd/MM/yyyy HH:mm or empty to keep): ");
            String dateInput = scanner.nextLine().trim();
            LocalDateTime expirationDate = dateInput.isBlank()
                    ? current.expirationDate() : parseDate(dateInput);

            TaskRequest request = new TaskRequest(
                    title, body, status, priority, expirationDate, current.eventId());
            taskService.update(id, request);
            System.out.println("Task #" + id + " updated successfully.");

        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteTask() {
        System.out.print("Enter Task ID to delete: ");
        Long id = readLongInput();
        if (id == null) return;

        try {
            taskService.delete(id);
            System.out.println("Task #" + id + " deleted.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findTaskById() {
        System.out.print("Enter Task ID: ");
        Long id = readLongInput();
        if (id == null) return;

        try {
            printTask(taskService.getById(id));
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayTasks(List<TaskResponse> tasks, String emptyMessage) {
        if (tasks.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            tasks.forEach(this::printTask);
        }
    }

    private void printTask(TaskResponse task) {
        System.out.printf("[#%d] %s | Status: %s | Priority: %s | Expires: %s%n",
                task.id(),
                task.title(),
                task.status(),
                task.priority() != null ? task.priority() : "N/A",
                task.expirationDate() != null ? task.expirationDate().format(DATE_FORMAT) : "N/A"
        );
    }

    private Priority parsePriority(String input, Priority defaultValue) {
        if (input.isBlank()) return defaultValue;
        try {
            return Priority.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid priority. Using:  " + (defaultValue != null ? defaultValue : "NONE"));
            return defaultValue;
        }
    }

    private Long readLongInput() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            return null;
        }
    }

    private LocalDateTime parseDate(String input) {
        try {
            return LocalDateTime.parse(input, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use: dd/MM/yyyy HH:mm");
            return null;
        }
    }
}