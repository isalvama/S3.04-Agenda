package org.agenda.event.controller;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventNotFoundException;
import org.agenda.event.repository.EventNotSavedException;
import org.agenda.event.service.EventService;
import org.agenda.shared.console.ConsoleReader;
import org.agenda.shared.domain.exception.DomainException;
import org.agenda.shared.exception.DataAccessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.agenda.shared.console.ConsoleReader.readLong;


public class EventController {

    private final EventService eventService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final EventFormatter eventFormatter = new EventFormatter();
    private final Map<String, Runnable> menuActions;


    public EventController(EventService eventService, Scanner scanner) {
        this.eventService = eventService;
        this.scanner = scanner;
        this.menuActions = new HashMap<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menuActions.put("1", this::createEvent);
        menuActions.put("2", this::listAllEvents);
        menuActions.put("3", this::listUpcomingEvents);
        menuActions.put("4", this::updateEvent);
        menuActions.put("5", this::deleteEvent);
        menuActions.put("6", this::findEventById);
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


    public void printMenu() {
        System.out.println("\n--- EVENT MANAGEMENT ---");
        System.out.println("1. Create an event");
        System.out.println("2. List all events");
        System.out.println("3. List upcoming events");
        System.out.println("4. Update an event");
        System.out.println("5. Delete an event");
        System.out.println("6. Find an event by ID");
        System.out.println("0. Back to main menu");
        System.out.println("Select an option:");
    }

    private void createEvent() {
        CreateEventRequest request = collectData("");
        try {
            EventResponse eventResponse = eventService.create(request);
            System.out.println("Event created with ID " + eventResponse.id() + ":\n" + eventFormatter.formatEventResponse(eventResponse));
        } catch (DomainException e) {
            System.out.println("Domain Error: " + e.getMessage());
        } catch (EventNotSavedException e) {
            System.out.println("Error In Saving Process: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error in DataBase: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Critical Error: " + e.getMessage());
        }
    }

    private void listAllEvents() {
        System.out.println("\n--- All Events ---");
        try {
            List<EventResponse> eventResponses = eventService.getAll();

            if (eventResponses.isEmpty()) {
                System.out.println("No events found");
                return;
            }

            System.out.println("List of found Events:\n" + eventFormatter.formatEventResponses(eventResponses));

        } catch (DomainException e) {
            System.out.println("Domain Error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error in DataBase: " + e.getMessage());
        }
    }

    private void listUpcomingEvents() {
        int days = ConsoleReader.readInt("\nEnter the number of days to display the next events (e.g., 5)");
        try {
            List<EventResponse> eventResponses = eventService.getUpcomingEvents(days);
            if (eventResponses.isEmpty()) {
                System.out.printf("There are no events in the next %s days", days);
                return;
            }
            System.out.println("List of Upcoming Events (in the next " + days + " days):\n" + eventFormatter.formatEventResponses(eventResponses));
        } catch (DomainException e) {
            System.out.println("Domain Error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error in DataBase: " + e.getMessage());
        }
    }

    private void updateEvent() {
        Long id = readLong("Enter the event ID to update: ");
        CreateEventRequest data = collectData("New ");
        UpdateEventRequest request = new UpdateEventRequest(id, data.title(), data.description(), data.date(), data.type(), data.eventSchedule());

        try {
            EventResponse eventResponse = eventService.update(request);
            System.out.printf("Task # %s updated successfully: %s", eventResponse.id(), eventFormatter.formatEventResponse(eventResponse));
        } catch (DomainException e) {
            System.out.println("Domain Error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error in DataBase process: " + e.getMessage());
        } catch (EventNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteEvent() {
        Long id = ConsoleReader.readLong("Enter an Event ID to delete: ");
        try {
            EventResponse eventResponse = eventService.delete(id);
            System.out.printf("Event #%s deleted successfully: %s", eventResponse.id(), eventFormatter.formatEventResponse(eventResponse));
        } catch (DomainException e) {
            System.out.println("Domain Error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error in DataBase: " + e.getMessage());
        } catch (EventNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findEventById() {
        Long id = ConsoleReader.readLong("Enter the Event ID to find: ");
        try {
            EventResponse response = eventService.getById(id);
            System.out.printf("Event #%s found successfully : %s", response.id(), eventFormatter.formatEventResponse(response));
        } catch (DomainException e) {
            System.out.println("Domain Error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error in DataBase: " + e.getMessage());
        } catch (EventNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private CreateEventRequest collectData(String wordToAddToQueries){

        String title = ConsoleReader.readString(String.format("%sTitle: ", wordToAddToQueries != null ? wordToAddToQueries : ""), 2);

        System.out.printf("%sBody: ", wordToAddToQueries != null ? wordToAddToQueries : "");
        String bodyInput = scanner.nextLine().trim();
        String body = bodyInput.isBlank() ? null : bodyInput;

        String typeInput = ConsoleReader.readEnumName(EventType.class, String.format("%sType of Event (BIRTHDATE / APPOINTMENT / REMINDER / or leave empty for OTHER): ", wordToAddToQueries != null ? wordToAddToQueries : ""));
        String type = typeInput != null ? typeInput : "OTHER";

        LocalDateTime date = ConsoleReader.readDate(String.format("%sEvent Date (dd/MM/yyyy HH:mm or leave empty to set it for tomorrow (+1 day)): %n", wordToAddToQueries != null ? wordToAddToQueries : ""), DATE_FORMAT, LocalDateTime.now().plusDays(1));

        String repetition = ConsoleReader.readEnumName(EventSchedule.class, String.format("%sSchedule a repetition for the event (YEARLY / MONTHLY / WEEKLY / DAILY / HOURLY or leave empty for none):", wordToAddToQueries != null ? wordToAddToQueries : ""));

        return new CreateEventRequest(title, body, date, type, repetition);
    }
}