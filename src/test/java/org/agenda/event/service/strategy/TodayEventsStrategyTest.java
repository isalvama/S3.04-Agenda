package org.agenda.event.service.strategy;

import org.agenda.event.dto.EventResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

class TodayEventsStrategyTest {
    ListEventsStrategy todayEventStrategy;

    @BeforeEach
    @DisplayName("Should only return the a sorted list of events with date of today")
    void setUp() {
        todayEventStrategy = new TodayEventsStrategy();
    }

    @Test
    void execute_ListOfTodayEventsSorted() {
        EventResponse event1 = new EventResponse(1L, "Event 1", "Desc", LocalDateTime.now().plusHours(1), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event2 = new EventResponse(2L, "Event 2", "Desc", LocalDateTime.now().plusDays(1), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event3 = new EventResponse(3L, "Event 3", "Desc", LocalDateTime.now().plusDays(8), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event4 = new EventResponse(4L, "Event 4", "Desc", LocalDateTime.now().plusMinutes(2), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event5 = new EventResponse(5L, "Event 5", "Desc", LocalDateTime.now().plusDays(6), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event6 = new EventResponse(6L, "Event 6", "Desc", LocalDateTime.now(), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event7 = new EventResponse(7L, "Event Past 1", "Desc", LocalDateTime.now().minusDays(1), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event8 = new EventResponse(8L, "Event Past 2", "Desc", LocalDateTime.now().minusDays(20), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event9 = new EventResponse(9L, "Event 9", "Desc", LocalDateTime.now().minusHours(2), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event10 = new EventResponse(10L, "Event 10", "Desc", LocalDateTime.now().minusMinutes(1), "OTHER", "No Schedule Set", Collections.emptyList());

        List<EventResponse> events = List.of(event1, event2, event3, event4, event5, event6, event7, event8, event9, event10);
        List<EventResponse> filteredEvents = todayEventStrategy.execute(events);

        Assertions.assertEquals(5, filteredEvents.size());

        Assertions.assertTrue(filteredEvents.contains(event1));
        Assertions.assertTrue(filteredEvents.contains(event4));
        Assertions.assertTrue(filteredEvents.contains(event6));
        Assertions.assertTrue(filteredEvents.contains(event9));
        Assertions.assertTrue(filteredEvents.contains(event10));

        Assertions.assertEquals(9L, filteredEvents.getFirst().id());
        Assertions.assertEquals(10L, filteredEvents.get(1).id());
        Assertions.assertEquals(6L, filteredEvents.get(2).id());
        Assertions.assertEquals(4L, filteredEvents.get(3).id());
        Assertions.assertEquals(1L, filteredEvents.get(4).id());
    }

    @Test
    @DisplayName("Should return an empty list")
    void execute_EmptyList() {
        EventResponse event1 = new EventResponse(1L, "Event 1", "Desc", LocalDateTime.now().plusDays(1), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event2 = new EventResponse(2L, "Event 2", "Desc", LocalDateTime.now().plusDays(8), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event3 = new EventResponse(3L, "Event 3", "Desc", LocalDateTime.now().plusDays(6), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event4 = new EventResponse(4L, "Event 4", "Desc", LocalDateTime.now().minusDays(1), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event5 = new EventResponse(5L, "Event 5", "Desc", LocalDateTime.now().minusDays(20), "OTHER", "No Schedule Set", Collections.emptyList());

        List<EventResponse> events = List.of(event2, event3, event4, event5);
        List<EventResponse> filteredEvents = todayEventStrategy.execute(events);

        Assertions.assertTrue(filteredEvents.isEmpty());
    }
}