package org.agenda.event.controller;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.repository.EventNotFoundException;
import org.agenda.event.service.EventService;
import org.agenda.shared.console.ConsoleReader;
import org.agenda.shared.domain.exception.DomainException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.agenda.shared.console.ConsoleReader.setScanner;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    private EventController eventController;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private void simulateInput(String data) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(data.getBytes()));
        ConsoleReader.setScanner(scanner);
    }

    @Test
    @DisplayName("Should display success message when an event is created successfully")
    void createEventSuccess() {
        String input = "1\nTeam Meeting\nProject Sync\nAPPOINTMENT\n\n\n0\n";
        simulateInput(input);
        EventResponse mockResponse = new EventResponse(1L, "Test Meeting", "Project Sync",
                LocalDateTime.now().plusDays(1), "APPOINTMENT", "No Schedule Set", List.of());

        when(eventService.create(any(CreateEventRequest.class))).thenReturn(mockResponse);

        eventController = new EventController(eventService, new Scanner(new ByteArrayInputStream(input.getBytes())));
        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Event created with ID 1"));
        verify(eventService, times(1)).create(any(CreateEventRequest.class));
    }

    @Test
    @DisplayName("Option 6: findEventById should show error message when event not found")
    void findEventById_NotFound() {
        String input = "6\n99\n0\n";
        simulateInput(input);

        when(eventService.getById(99L)).thenThrow(new EventNotFoundException("Event not found", 99L));

       eventController = new EventController(eventService, new Scanner(new ByteArrayInputStream(input.getBytes())));
        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Error: "));
        verify(eventService).getById(99L);
    }

    @Test
    @DisplayName("Option 5: deleteEvent should show success message")
    void deleteEvent_Success() {
        String input = "5\n10\n0\n";
        simulateInput(input);
        EventResponse deletedResponse = new EventResponse(10L, "To Delete", null,
                LocalDateTime.now(), "OTHER", null, List.of());

        when(eventService.delete(10L)).thenReturn(deletedResponse);

        eventController = new EventController(eventService, new Scanner(new ByteArrayInputStream(input.getBytes())));
        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Event #10 deleted successfully"));
        verify(eventService).delete(10L);
    }

    @Test
    @DisplayName("Option 1: createEvent should handle DomainException")
    void createEvent_DomainError() {
        String input = "1\nInvalidTitle\nSomeBody\nOTHER\n\n\n0\n";
        simulateInput(input);

        when(eventService.create(any())).thenThrow(new DomainException("Invalid Title format"));

        eventController = new EventController(eventService, new Scanner(new ByteArrayInputStream(input.getBytes())));
        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Domain Error: Invalid Title format"));
    }
}