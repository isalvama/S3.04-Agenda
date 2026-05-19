package org.agenda.event.controller;

import org.agenda.event.dto.EventResponse;
import org.agenda.event.service.EventService;
import org.agenda.shared.domain.exception.DomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventNotifierTest {

    @Mock
    private EventService eventService;

    private EventNotifier eventNotifier;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        eventNotifier = new EventNotifier(eventService);
    }

    @AfterEach
    void restore() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should show 'No events found' when service returns an empty list")
    void notifyAllEvents_EmptyList() {
        when(eventService.getAll()).thenReturn(Collections.emptyList());

        eventNotifier.notifyAllEvents();

        assertTrue(outContent.toString().contains("No events found"));
        verify(eventService, times(1)).getAll();
    }

    @Test
    @DisplayName("Should show error message when service throws an exception")
    void notifyAllEvents_ServiceError() {
        when(eventService.getAll()).thenThrow(new DomainException("Database connection failed"));

        eventNotifier.notifyAllEvents();

        assertTrue(outContent.toString().contains("Error Fetching Events: Database connection failed"));
    }

    @Test
    @DisplayName("Should show events in their respective sections")
    void notifyAllEvents_WithData() {
        LocalDateTime now = LocalDateTime.now();
        EventResponse pastEvent = new EventResponse(3L, "Yesterday's Meeting", "Desc", now, "OTHER", null, List.of());
        EventResponse todayEvent = new EventResponse(1L, "Today's Meeting", "Desc", now, "OTHER", null, List.of());
        EventResponse futureEvent = new EventResponse(2L, "Next Week", "Desc", now.plusDays(5), "OTHER", null, List.of());

        when(eventService.getAll()).thenReturn(List.of(pastEvent, todayEvent, futureEvent));

        eventNotifier.notifyAllEvents();

        String output = outContent.toString();

        assertTrue(output.contains("Last Two Days Events"));
        assertTrue(output.contains("Today Events"));
        assertTrue(output.contains("Next Seven Days Events"));

        assertTrue(output.contains("Yesterday's Meeting"));
        assertTrue(output.contains("Today's Meeting"));
        assertTrue(output.contains("Next Week"));
    }

    @Test
    @DisplayName("Should show 'No events during this time period' when a section is empty")
    void notifyAllEvents_PartialEmptySections() {
        EventResponse todayEvent = new EventResponse(1L, "Only Today", "Desc", LocalDateTime.now(), "OTHER", null, List.of());
        when(eventService.getAll()).thenReturn(List.of(todayEvent));

        eventNotifier.notifyAllEvents();

        String output = outContent.toString();
        assertTrue(output.contains("No events during this time period"));
    }
}