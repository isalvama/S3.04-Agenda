package org.agenda.event.model;

import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class CalendarEventTest {

    @Test
    void create_shouldReturnACalendarEventObjectWithNullIdAndIntroducedValues() {
        Title title = Title.of("Event 1");
        Description desc = Description.of("Desc");
        LocalDateTime date = LocalDateTime.of(2026, 7, 10, 9, 0);
        EventType type = EventType.APPOINTMENT;
        EventSchedule schedule = EventSchedule.YEARLY;

        CalendarEvent newEvent = CalendarEvent.create(title, desc, date, type, schedule);
        Assertions.assertNull(newEvent.getId());
        Assertions.assertEquals(title, newEvent.getTitle());
        Assertions.assertEquals(desc, newEvent.getDescription().orElse(null));
        Assertions.assertEquals(date, newEvent.getDate());
        Assertions.assertEquals(type, newEvent.getType());
        Assertions.assertEquals(schedule, newEvent.getSchedule().orElse(null));
    }

    @Test
    void create_shouldReturnACalendarEventObjectWithNullDescAndSchedule() {
        Title title = Title.of("Event 1");
        LocalDateTime date = LocalDateTime.of(2026, 7, 10, 9, 0);
        EventType type = EventType.APPOINTMENT;

        CalendarEvent newEvent = CalendarEvent.create(title, null, date, type, null);
        Assertions.assertNull(newEvent.getId());
        Assertions.assertEquals(title, newEvent.getTitle());
        Assertions.assertFalse(newEvent.getDescription().isPresent());
        Assertions.assertEquals(date, newEvent.getDate());
        Assertions.assertEquals(type, newEvent.getType());
        Assertions.assertFalse(newEvent.getSchedule().isPresent());
    }

    @Test
    void constructor_shouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, ()->{ new CalendarEvent(null, null, Description.of("Description"), LocalDateTime.now(), EventType.APPOINTMENT, EventSchedule.YEARLY);});
        Assertions.assertThrows(NullPointerException.class, ()->{ new CalendarEvent(null, Title.of("Title"), Description.of("Description 2"), null, EventType.APPOINTMENT, EventSchedule.MONTHLY);});
        Assertions.assertThrows(NullPointerException.class, ()->{ new CalendarEvent(null, Title.of("Title 2"), Description.of("Description 3"), LocalDateTime.now(), null, EventSchedule.DAILY);});
    }

    @Test
    void checkIfDateIsInThePast() {
        CalendarEvent event1 = CalendarEvent.create(Title.of("Title Event"), null, LocalDateTime.now().minusMinutes(2), EventType.APPOINTMENT, null);
        CalendarEvent event2 = CalendarEvent.create(Title.of("Title Event"), null, LocalDateTime.now().plusMinutes(2), EventType.APPOINTMENT, null);

        Assertions.assertTrue(event1.checkIfDateIsInThePast(LocalDateTime.now()).isPresent());
        Assertions.assertFalse(event2.checkIfDateIsInThePast(LocalDateTime.now()).isPresent());
    }

    @Test
    void createNextOccurrence_ShouldReturnNewDateAccordingToSchedule() {
        CalendarEvent event = new CalendarEvent(1L, Title.of("Event"), Description.of("Description"), LocalDateTime.now(), EventType.BIRTHDATE, EventSchedule.YEARLY);
        CalendarEvent newEvent = event.createNextOccurrence();
        Assertions.assertEquals(LocalDate.now().plusYears(1), newEvent.getDate().toLocalDate());

        CalendarEvent event2 = new CalendarEvent(2L, Title.of("Event 2"), Description.of("Description"), LocalDateTime.now(), EventType.APPOINTMENT, EventSchedule.DAILY);
        CalendarEvent newEvent2 = event2.createNextOccurrence();
        Assertions.assertEquals(LocalDate.now().plusDays(1), newEvent2.getDate().toLocalDate());

        CalendarEvent event3 = new CalendarEvent(3L, Title.of("Event 3"), Description.of("Description"), LocalDateTime.now(), EventType.REMINDER, EventSchedule.MONTHLY);
        CalendarEvent newEvent3 = event3.createNextOccurrence();
        Assertions.assertEquals(LocalDate.now().plusMonths(1), newEvent3.getDate().toLocalDate());

        CalendarEvent event4 = new CalendarEvent(4L, Title.of("Event 4"), Description.of("Description"), LocalDateTime.now(), EventType.APPOINTMENT, EventSchedule.WEEKLY);
        CalendarEvent newEvent4 = event4.createNextOccurrence();
        Assertions.assertEquals(LocalDate.now().plusWeeks(1), newEvent4.getDate().toLocalDate());

        CalendarEvent event5 = new CalendarEvent(5L, Title.of("Event 5"), Description.of("Description"), LocalDateTime.now(), EventType.REMINDER, EventSchedule.HOURLY);
        CalendarEvent newEvent5 = event5.createNextOccurrence();
        Assertions.assertEquals(LocalDateTime.now().plusHours(1).getHour(), newEvent5.getDate().getHour());
    }

    @Test
    void createNextOccurrence_ShouldReturnNullIfEventHasNullSchedule() {
       CalendarEvent event = new CalendarEvent(1L, Title.of("Event"), Description.of("Description"), LocalDateTime.now(), EventType.APPOINTMENT, null);
       Assertions.assertNull(event.createNextOccurrence());
    }
}