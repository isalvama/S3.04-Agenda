package org.agenda.event.service.strategy;

import org.agenda.event.controller.EventNotifier;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.service.EventService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NextSevenDaysEventsStrategyTest {
    @Mock
    private ListEventsStrategy nextSevenDaysEventsStrategy;

    @BeforeEach
    void setUp() {
        nextSevenDaysEventsStrategy = new NextSevenDaysEventsStrategy();
    }

    @Test
    @DisplayName("Should only return the events sorted with dates falling within the next 7 days")
    void execute_ListOfNextSevenDaysEventsSorted() {
        EventResponse event1 = new EventResponse(1L, "Event 1", "Desc", LocalDateTime.now(), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event2 = new EventResponse(2L, "Event 2", "Desc", LocalDateTime.now().plusDays(1), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event3 = new EventResponse(3L, "Event 3", "Desc", LocalDateTime.now().plusDays(8), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event4 = new EventResponse(4L, "Event 4", "Desc", LocalDateTime.now().plusDays(4), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event5 = new EventResponse(5L, "Event 5", "Desc", LocalDateTime.now().plusDays(6), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse event6 = new EventResponse(6L, "Event 6", "Desc", LocalDateTime.now().plusDays(7), "OTHER", "No Schedule Set", Collections.emptyList());
        EventResponse pastEvent = new EventResponse(7L, "Event Past", "Desc", LocalDateTime.now().minusDays(1), "OTHER", "No Schedule Set", Collections.emptyList());


        List<EventResponse> events = List.of(event1, event2, event3, event4, event5, event6, pastEvent);
        List<EventResponse> filteredEvents = nextSevenDaysEventsStrategy.execute(events);

        Assertions.assertEquals(4, filteredEvents.size());

        Assertions.assertTrue(filteredEvents.contains(event2));
        Assertions.assertTrue(filteredEvents.contains(event4));
        Assertions.assertTrue(filteredEvents.contains(event5));
        Assertions.assertTrue(filteredEvents.contains(event6));

        Assertions.assertEquals(2L, filteredEvents.getFirst().id());
        Assertions.assertEquals(4L, filteredEvents.get(1).id());
        Assertions.assertEquals(5L, filteredEvents.get(2).id());
        Assertions.assertEquals(6L, filteredEvents.get(3).id());
    }
}