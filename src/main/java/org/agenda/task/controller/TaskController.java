package org.agenda.task.controller;

import org.agenda.task.dto.TaskRequest;
import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Priority;
import org.agenda.task.model.Status;
import org.agenda.task.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TaskController {

    private final TaskService taskService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TaskController(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
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

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createTask();
                case "2" -> listAllTasks();
                case "3" -> listPendingTasks();
                case "4" -> listCompletedTasks();
                case "5" -> markTaskAsDone();
                case "6" -> updateTask();
                case "7" -> deleteTask();
                case "8" -> findTaskById();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createTask() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        System.out.print("Priority (HIGH / MEDIUM / LOW or leave empty): ");
        String priorityInput = scanner.nextLine().trim();
        Priority priority = priorityInput.isBlank() ? null : Priority.valueOf(priorityInput.toUpperCase());

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
        List<TaskResponse> tasks = taskService.getAll();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        tasks.forEach(this::printTask);
    }

    private void listPendingTasks() {
        System.out.println("\n--- Pending Tasks ---");
        List<TaskResponse> tasks = taskService.getPendingTasks();
        if (tasks.isEmpty()) {
            System.out.println("No pending tasks.");
            return;
        }
        tasks.forEach(this::printTask);
    }

    private void listCompletedTasks() {
        System.out.println("\n--- Completed Tasks ---");
        List<TaskResponse> tasks = taskService.getCompletedTasks();
        if (tasks.isEmpty()) {
            System.out.println("No completed tasks.");
            return;
        }
        tasks.forEach(this::printTask);
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
            System.out.println("Current task: " + current.title());

            System.out.print("New title (leave empty to keep current): ");
            String title = scanner.nextLine().trim();
            if (title.isBlank()) title = current.title();

            System.out.print("New body (leave empty to keep current): ");
            String body = scanner.nextLine().trim();
            if (body.isBlank()) body = current.body();

            System.out.print("New status (PENDING / IN PROGRESS / DONE or leave empty): ");
            String statusInput = scanner.nextLine().trim();
            Status status = statusInput.isBlank() ? current.status() : Status.fromSqlValue(statusInput);

            System.out.print("New priority (HIGH / MEDIUM / LOW or leave empty): ");
            String priorityInput = scanner.nextLine().trim();
            Priority priority = priorityInput.isBlank() ? current.priority() : Priority.valueOf(priorityInput.toUpperCase());

            System.out.print("New expiration date (dd/MM/yyyy HH:mm or leave empty): ");
            String dateInput = scanner.nextLine().trim();
            LocalDateTime expirationDate = dateInput.isBlank() ? current.expirationDate() : parseDate(dateInput);

            TaskRequest request = new TaskRequest(title, body, status, priority, expirationDate, current.eventId());
            TaskResponse response = taskService.update(id, request);
            System.out.println("Task #" + response.id() + " updated successfully.");

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
            TaskResponse response = taskService.getById(id);
            printTask(response);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
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