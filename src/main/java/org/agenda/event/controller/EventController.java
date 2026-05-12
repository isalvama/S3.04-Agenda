package org.agenda.event.controller;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventRepository;
import org.agenda.event.service.EventService;
import org.agenda.shared.console.ConsoleReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class EventController {

    private final EventService eventService;
    private final EventRepository repository;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    public EventController(EventService eventService, EventRepository repository, Scanner scanner) {
        this.eventService = eventService;
        this.scanner = scanner;
        this.repository = repository;
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- EVENT MANAGEMENT ---");
            System.out.println("1. Create an event");
            System.out.println("2. List all events");
            System.out.println("3. List upcoming events");
            System.out.println("4. Update an event");
            System.out.println("5. Delete an event");
            System.out.println("6. Find an event by ID");
            System.out.println("0. Back to main menu");
            System.out.println("Select an option:");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createEvent();
                case "2" -> listAllEvents();
                case "3" -> listUpcomingEvents();
                case "4" -> updateEvent();
                case "5" -> deleteEvent();
                case "6" -> findEventById();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createEvent() {
        String title = ConsoleReader.validateString("Title: ");

        System.out.print("Body: ");
        String bodyInput = scanner.nextLine().trim();
        String body = bodyInput.isBlank() ? null : bodyInput;

        System.out.print("Type of Event (BIRTHDATE / APPOINTMENT / REMINDER / or leave empty for OTHER): ");
        String typeOfEventInput = scanner.nextLine().trim();
        String eventType = typeOfEventInput.isBlank() ? null : validateName(typeOfEventInput, EventType.class);

        String date = ConsoleReader.validateString("Event Date (dd/MM/yyyy HH:mm or leave empty to set it for tomorrow (+1 day)): ");
        String dateInput = scanner.nextLine().trim();
        LocalDateTime eventDate = dateInput.isBlank()
                ? LocalDateTime.now().plusDays(1)
                : parseDate(dateInput);

        System.out.print("Schedule a repetition for the event (YEARLY / MONTHLY / WEEKLY / DAILY / HOURLY or leave empty for none): ");
        String repetition = scanner.nextLine().trim();
        repetition = repetition.isBlank()
                ? null
                : validateName(repetition, EventSchedule.class);

        CreateEventRequest request = new CreateEventRequest(title, body, eventDate, eventType, repetition);

        try {
            EventResponse response = eventService.create(request);
            System.out.printf("Event \"%s\" created with ID %s for date %s. %s%n", response.title(), response.id(), response.date(), response.warnings());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAllEvents() {
        System.out.println("\n--- All Events ---");
        List<EventResponse> events = eventService.getAll();
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        events.forEach(this::printEvent);
    }

    private void listUpcomingEvents() {
        int days = ConsoleReader.readInt("\nEnter the number of days to display the next events (e.g., 7).");
        List<EventResponse> events = eventService.getUpcomingEvents(days);
        if (events.isEmpty()) {
            System.out.printf("There are no events in the next %s days", days);
            return;
        }
        events.forEach(this::printEvent);
    }

    private void updateEvent() {
        Long id = ConsoleReader.readLong("Enter the event ID to update: ");
        CreateEventRequest data = collectData("New ");
        UpdateEventRequest request = new UpdateEventRequest(id, data.title(), data.description(), data.date(), data.type(), data.eventSchedule()); // TODO PONERLO EN OTRO OBJ

        try {
            EventResponse response = eventService.update(request);
            System.out.printf("Task # %s updated successfully: \nTile: %s\nDate: %s\n%s", response.id(), response.title(), response.date(), response.warnings());

        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteEvent() {
        System.out.print("Enter Event ID to delete: ");
        Long id = readLongInput();
        if (id == null) return;

        try {
            eventService.delete(id);
            System.out.println("Event #" + id + " deleted.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findEventById() {
        System.out.print("Enter Event ID: ");
        Long id = readLongInput();
        if (id == null) return;

        try {
            EventResponse response = eventService.getById(id);
            printEvent(response);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private CreateEventRequest collectData(String wordToAddToQueries){
        String title = ConsoleReader.validateString(String.format("%sTitle: ", wordToAddToQueries != null ? wordToAddToQueries : ""));

        System.out.printf("%sBody: ", wordToAddToQueries != null ? wordToAddToQueries : "");
        String bodyInput = scanner.nextLine().trim();
        String body = bodyInput.isBlank() ? null : bodyInput;

        System.out.printf("%sType of Event (BIRTHDATE / APPOINTMENT / REMINDER / or leave empty for OTHER): ", wordToAddToQueries != null ? wordToAddToQueries : "");
        String typeOfEventInput = scanner.nextLine().trim();
        String eventType = typeOfEventInput.isBlank() ? null : validateName(typeOfEventInput, EventType.class);

        String date = ConsoleReader.validateString(String.format("%sEvent Date (dd/MM/yyyy HH:mm or leave empty to set it for tomorrow (+1 day)): ", wordToAddToQueries != null ? wordToAddToQueries : ""));
        String dateInput = scanner.nextLine().trim();
        LocalDateTime eventDate = dateInput.isBlank()
                ? LocalDateTime.now().plusDays(1)
                : parseDate(dateInput);

        System.out.printf("%sSchedule a repetition for the event (YEARLY / MONTHLY / WEEKLY / DAILY / HOURLY or leave empty for none):", wordToAddToQueries != null ? wordToAddToQueries : "");
        String repetition = scanner.nextLine().trim();
        repetition = repetition.isBlank()
                ? null
                : validateName(repetition, EventSchedule.class);

        return new CreateEventRequest(title, body, eventDate, eventType, repetition);
    }

    private void printEvent(EventResponse eventResponse) {
        System.out.printf("Id: %s | Title: %s | Date: %s",
                eventResponse.id(),
                eventResponse.title(),
                eventResponse.date()
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
        while (true) {
            try {
                return LocalDateTime.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use: dd/MM/yyyy HH:mm");
            }
        }
    }

    private <T extends Enum<T>>String validateName (String name, Class<T> enumClass){
        if (Arrays.stream(enumClass.getEnumConstants()).noneMatch(v -> v.toString().equalsIgnoreCase(name))) throw new IllegalArgumentException(); //TODO
        return name;
    }
}
