package org.agenda.event.service;

import org.agenda.event.dto.EventResponse;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class EventResponseMapperTest {
    EventResponseMapper responseMapper = new EventResponseMapper();

    @Test
    void eventWithNullDescriptionAndScheduleShouldCreateResponseModel(){
        String titleRaw = "Meal With Mum";
        String typeRaw = "APPOINTMENT";
        Long id = 2L;
        Title title = Title.of("Meal With Mum");
        LocalDateTime date= LocalDateTime.of(2026, 3, 13, 6, 9);
        EventType type = EventType.valueOf("APPOINTMENT");
        CalendarEvent event = new CalendarEvent(id, title, null, date, type, null);
        EventResponse response = responseMapper.toResponseModel(event);
        Assertions.assertEquals(id, response.id());
        Assertions.assertEquals(titleRaw, response.title());
        Assertions.assertEquals("No Desc", response.description());
        Assertions.assertEquals(date, response.date());
        Assertions.assertEquals(typeRaw, response.type());
        Assertions.assertEquals("No Schedule Set", response.schedule());
    }

    @Test
    void eventWithAllAttributesAndWarningShouldCreateResponseModel(){
        Long id = 100L;
        String titleRaw = "Anna's Birthday";
        String descRaw = "Should buy the book she wanted";
        LocalDateTime date = LocalDateTime.of(2026, 5, 16, 9, 0);
        String typeRaw = "REMINDER";
        String scheduleRaw = "YEARLY";
        CalendarEvent event = new CalendarEvent(id, Title.of(titleRaw), Description.of(descRaw), date, EventType.valueOf(typeRaw), EventSchedule.valueOf(scheduleRaw));
        List<String> warnings = List.of("Note: this event date is in the past");
        EventResponse response = responseMapper.toResponseModel(event, warnings);
        Assertions.assertEquals(id, response.id());
        Assertions.assertEquals(titleRaw, response.title());
        Assertions.assertEquals(descRaw, response.description());
        Assertions.assertEquals(date, response.date());
        Assertions.assertEquals(typeRaw, response.type());
        Assertions.assertEquals(scheduleRaw, response.schedule());
        Assertions.assertEquals(warnings, response.warnings());
    }

    @Test
    void shouldCreateResponseModel(){
        Long id1 = 100L;
        String titleRaw1 = "Anna's Birthday";
        String descRaw1 = "Should buy the book she wanted";
        LocalDateTime date1 = LocalDateTime.of(2026, 5, 16, 9, 0);
        String typeRaw1 = "REMINDER";
        String scheduleRaw1 = "YEARLY";
        CalendarEvent event1 = new CalendarEvent(id1, Title.of(titleRaw1), Description.of(descRaw1), date1, EventType.valueOf(typeRaw1), EventSchedule.valueOf(scheduleRaw1));
        List<String> warnings = List.of("Note: this event date is in the past");

        Long id2 = 102L;
        String titleRaw2 = "Merge Call";
        String descRaw2 = "Review TODO List";
        LocalDateTime date2 = LocalDateTime.of(2026, 5, 17, 14, 0);
        String typeRaw2 = "APPOINTMENT";
        CalendarEvent event2 = new CalendarEvent(id2, Title.of(titleRaw2), Description.of(descRaw2), date2, EventType.valueOf(typeRaw2), null);

        List<CalendarEvent> events = List.of(event1, event2);

        List<EventResponse> responses = responseMapper.toResponseModels(events);
        Assertions.assertEquals(id1, responses.getFirst().id());
        Assertions.assertEquals(titleRaw1, responses.getFirst().title());
        Assertions.assertEquals(descRaw1, responses.getFirst().description());
        Assertions.assertEquals(date1, responses.getFirst().date());
        Assertions.assertEquals(typeRaw1, responses.getFirst().type());
        Assertions.assertEquals(scheduleRaw1, responses.getFirst().schedule());

        Assertions.assertEquals(id2, responses.get(1).id());
        Assertions.assertEquals(titleRaw2, responses.get(1).title());
        Assertions.assertEquals(descRaw2, responses.get(1).description());
        Assertions.assertEquals(date2, responses.get(1).date());
        Assertions.assertEquals(typeRaw2, responses.get(1).type());
        Assertions.assertEquals("No Schedule Set", responses.get(1).schedule());
    }
}