package org.agenda.event.controller;

import org.agenda.event.dto.EventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventFormatterTest {

    private EventFormatter formatter;
    private final LocalDateTime fixedDate = LocalDateTime.of(2025, 5, 20, 10, 0);

    @BeforeEach
    void setUp() {
        formatter = new EventFormatter();
    }

    @Test
    @DisplayName("Should format a single event response correctly")
    void formatEventResponse_CorrectFormat() {
        EventResponse response = new EventResponse(
                1L,
                "Meeting",
                "Work stuff",
                fixedDate,
                "OTHER",
                "WEEKLY",
                List.of("Warning 1")
        );

        String result = formatter.formatEventResponse(response);

        String expected = "Id: 1 | Title: Meeting | Description: Work stuff | Date: 20/05/2025 10:00 | Type: OTHER | Schedule: WEEKLY. Warning 1\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle empty warnings list correctly")
    void formatEventResponse_EmptyWarnings() {
        EventResponse response = new EventResponse(
                2L, "Gym", "Leg day", fixedDate, "OTHER", "DAILY", Collections.emptyList()
        );

        String result = formatter.formatEventResponse(response);

        assertTrue(result.contains("No warnings"));
    }

    @Test
    @DisplayName("Should return \"No events to display\" string when formatting an empty list of events")
    void formatEventResponses_EmptyList() {
        String result = formatter.formatEventResponses(Collections.emptyList());

        assertTrue(result.contains("No events to display"));
    }

    @Test
    @DisplayName("Should concatenate multiple events with line breaks")
    void formatEventResponses_MultipleEvents() {
        EventResponse e1 = new EventResponse(1L, "E1", "No Desc", fixedDate, "OTHER", "YEARLY", List.of());
        EventResponse e2 = new EventResponse(2L, "E2", "D2", fixedDate, "OTHER", "No Schedule Set", List.of());
        List<EventResponse> list = List.of(e1, e2);

        String result = formatter.formatEventResponses(list);

        String[] lines = result.split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].contains("Id: 1"));
        assertTrue(lines[1].contains("Id: 2"));
    }

    @Test
    @DisplayName("Should handle default values for descriptions or schedules if the record allows them")
    void formatEventResponse_WithDefaultValues() {
        EventResponse response = new EventResponse(
                1L, "No Details", "No Desc", fixedDate, "OTHER", "No Schedule Set", List.of()
        );

        String result = formatter.formatEventResponse(response);

        assertTrue(result.contains("Description: No Desc"));
        assertTrue(result.contains("Schedule: No Schedule Set"));
    }
}