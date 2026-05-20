package org.agenda.note.controller;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.dto.NoteResponse;

import org.agenda.note.exception.NoteNotFoundException;
import org.agenda.note.service.NoteService;
import org.agenda.shared.console.ConsoleReader;

import org.agenda.task.service.TaskService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NoteController {
    private final NoteService service;
    private final TaskService taskService;
    private final Map<String, Runnable> menuActions;
    private final Scanner scanner;

    public NoteController(NoteService service, TaskService taskService, Scanner scanner) {
        this.service = service;
        this.taskService = taskService;
        this.scanner = scanner;
        this.menuActions = new HashMap<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menuActions.put("1", this::create);
        menuActions.put("2", this::getAll);
        menuActions.put("3", this::getById);
        menuActions.put("4", this::getAllByTaskId);
        menuActions.put("5", this::update);
        menuActions.put("6", this::delete);
        menuActions.put("0", () -> System.out.println("Returning to main menu..."));
    }
    public void showMenu(){
        boolean running = true;
        while (running) {
            printMenu();
            String option = ConsoleReader.readString("Choose an option:", 1);

            if ("0".equals(option)) {
                System.out.println("Returning to main menu...");
                running = false;
            } else {
                menuActions.getOrDefault(option,
                        () -> System.out.println("Invalid option. Please try again.")).run();
            }
        }
    }

    private void printMenu() {
        System.out.println("--- NOTES MENU ---");
        System.out.println("==============================");
        System.out.println("1. Create Note");
        System.out.println("2. List All Notes");
        System.out.println("3. View Note by ID");
        System.out.println("4. List Notes by Task ID");
        System.out.println("5. Update Note");
        System.out.println("6. Delete Note");
        System.out.println("0. Back");
        System.out.println("==============================");
    }

    public void create() {
        try {
            System.out.println("--- CREATE NOTE ---");
            System.out.println("Available Tasks: ");
            taskService.getAll().forEach(task ->
                    System.out.printf("[#%d] %s%n", task.id(), task.title())
            );

            Long taskId = ConsoleReader.readLong("Choose a task ID:");

            if (!taskService.existsById(taskId)) {
                System.out.println("Task does not exist. Cannot create note.");
                return;
            }

            NoteRequest request = collectNoteData(taskId);
            NoteResponse response = service.create(request);

            System.out.println("\nNote created successfully:");
            printNote(response);

        } catch (Exception e) {
            System.out.println("Error creating note: " + e.getMessage());
        }
    }

    public void getById() {
        try {
            System.out.println("--- NOTE ---");
            Long id = ConsoleReader.readLong("Note ID: ");
            NoteResponse response = service.getById(id);
            printNote(response);

        } catch (NoteNotFoundException e) {
            System.out.println("Note not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error fetching note: " + e.getMessage());
        }
    }

    public void getAll() {
        try {
            System.out.println("--- NOTES LIST ---");
            List<NoteResponse> notes = service.getAll();

            if (notes.isEmpty()) {
                System.out.println("No notes found.");
                return;
            }

            notes.forEach(this::printNote);
        } catch (Exception e) {
            System.out.println("Error listing notes: " + e.getMessage());
        }
    }

    public void getAllByTaskId() {
        try {
            System.out.println("--- NOTES LIST ---");
            System.out.println("Available Tasks: ");
            taskService.getAll().forEach(task ->
                    System.out.printf("[#%d] %s%n", task.id(), task.title())
            );

            Long taskId = ConsoleReader.readLong("Task ID: ");
            List<NoteResponse> notes = service.getAllByTaskId(taskId);

            if (notes.isEmpty()) {
                System.out.println("No notes found for this task.");
                return;
            }

            notes.forEach(this::printNote);

        } catch (Exception e) {
            System.out.println("Error fetching notes by task: " + e.getMessage());
        }
    }

    public void update() {
        try {
            System.out.println("--- UPDATE NOTE ---");
            Long id = ConsoleReader.readLong("Note ID: ");

            System.out.println("Available Tasks: ");
            taskService.getAll().forEach(task ->
                    System.out.printf("[#%d] %s%n", task.id(), task.title())
            );

            Long taskId = ConsoleReader.readLong("Task ID: ");

            if (!taskService.existsById(taskId)) {
                System.out.println("Task does not exist. Cannot update note.");
                return;
            }

            NoteRequest request = collectNoteData(taskId);
            NoteResponse response = service.update(id, request);

            System.out.println("Note updated successfully:");
            printNote(response);

        } catch (NoteNotFoundException e) {
            System.out.println("Note not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating note: " + e.getMessage());
        }
    }

    public void delete() {
        try {
            System.out.println("--- DELETE NOTE ---");
            Long id = ConsoleReader.readLong("Note ID: ");
            boolean deleted = service.delete(id);

            if (deleted) {
                System.out.println("Note deleted successfully.");
            } else {
                System.out.println("Note could not be deleted.");
            }
        } catch (NoteNotFoundException e) {
            System.out.println("Note not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error deleting note: " + e.getMessage());
        }
    }

    private NoteRequest collectNoteData(Long taskId) {
        String title = ConsoleReader.readString("Title: ", 2);
        System.out.println("Body (optional, press Enter to skip): ");
        String bodyInput = scanner.nextLine().trim();
        String body = bodyInput.isBlank() ? null : bodyInput;
        return new NoteRequest(title, body, taskId);
    }

    private void printNote(NoteResponse noteResponse) {
        System.out.printf("[#%d] | Task: %d | Title: %s | Body: %s%n",
                noteResponse.id(),
                noteResponse.taskId(),
                noteResponse.title(),
                noteResponse.body()
        );
    }

}
