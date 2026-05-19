package org.agenda.event.controller;

import org.agenda.event.dto.EventResponse;
import org.agenda.event.repository.EventNotFoundException;
import org.agenda.event.service.EventService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    private EventController eventController;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restore() {
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        eventController = new EventController(eventService, new Scanner(System.in));
    }

    @Test
    @DisplayName("Option 2: listAllEvents should show events when they exist")
    void listAllEvents_Success() {
        provideInput("2\n0\n");
        EventResponse mockResponse = new EventResponse(1L, "Test Event", "Desc",
                LocalDateTime.now(), "OTHER", "NONE", List.of());

        when(eventService.getAll()).thenReturn(List.of(mockResponse));

        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("List of found Events:"));
        assertTrue(output.contains("Test Event"));
        verify(eventService).getAll();
    }

    @Test
    @DisplayName("Option 6: findEventById should show error message when event not found")
    void findEventById_NotFound() {
        provideInput("6\n99\n0\n");

        when(eventService.getById(99L)).thenThrow(new EventNotFoundException("Event not found", 99L));

        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Error: Event 99 not found"));
        verify(eventService).getById(99L);
    }

    @Test
    @DisplayName("Option 5: deleteEvent should show success message")
    void deleteEvent_Success() {
        provideInput("5\n10\n0\n");
        EventResponse deletedResponse = new EventResponse(10L, "To Delete", null,
                LocalDateTime.now(), "OTHER", null, List.of());

        when(eventService.delete(10L)).thenReturn(deletedResponse);

        eventController.showMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Event #10 deleted successfully"));
        verify(eventService).delete(10L);
    }

    @Test
    @DisplayName("Option 1: createEvent should handle DomainException")
    void createEvent_DomainError() {
        provideInput("1\nInvalidTitle\nSomeBody\nOTHER\n\n\n0\n");

        when(eventService.create(any())).thenThrow(new DomainException("Invalid Title format"));

        // Act
        eventController.showMenu();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Domain Error: Invalid Title format"));
    }
}