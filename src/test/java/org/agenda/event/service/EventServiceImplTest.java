package org.agenda.event.service;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventNotFoundException;
import org.agenda.event.repository.EventNotSavedException;
import org.agenda.event.repository.EventRepository;
import org.agenda.event.repository.EventRepositoryImpl;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @DisplayName("When create() is called passing a CreateEventRequest with a description and a schedule, the method should return a ResponseEvent object with all values")
    @Test
    void createShouldReturnResponseEvent() {
        Long id = 1L;
        String titleRaw = "Maria's birthday party";
        String descriptionRaw = "Should wear a costume";
        LocalDateTime date = LocalDateTime.of(2028, 2, 18, 18, 0);
        String typeRaw = "BIRTHDATE";
        String scheduleRaw = "YEARLY";
        List<String> warnings = List.of();
        CreateEventRequest eventRequestInput = new CreateEventRequest(titleRaw, descriptionRaw, date, typeRaw, scheduleRaw);
        CalendarEvent event = new CalendarEvent(id, Title.of(titleRaw), Description.of(descriptionRaw), date, EventType.BIRTHDATE, EventSchedule.YEARLY);
        EventResponse expectedResponse = new EventResponse(id, "Maria's Birthday Party", descriptionRaw, date, typeRaw, scheduleRaw, warnings);

        Mockito.when(eventRepository.save(any(CalendarEvent.class))).thenReturn(Optional.of(event));
        EventResponse result = eventService.create(eventRequestInput);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.title(), result.title());
        assertEquals(expectedResponse.description(), result.description());
        assertEquals(expectedResponse.date(), result.date());
        assertEquals(expectedResponse.type(), result.type());
        assertEquals(expectedResponse.schedule(), result.schedule());
        Assertions.assertTrue(result.warnings().isEmpty());
        assertEquals(expectedResponse.warnings(), result.warnings());
        Mockito.verify(eventRepository).save(any());
    }

    @DisplayName("When create() is called passing a CreateEventRequest with past event and null description, type and eventSchedule, the method should return a ResponseEvent object with info of no Desc and No Schedule Set, and OTHER as eventType")
    @Test
    void createShouldReturnEventResponseWithInfoOfNoDescriptionAndSchedule() {
        Long id = 1L;
        String titleRaw = "Work Meeting";
        LocalDateTime date = LocalDateTime.of(2026, 3, 14, 9, 0);
        String typeRaw = "OTHER";
        List<String> warnings = List.of("Note: this event date is in the past");
        CreateEventRequest eventRequestInput = new CreateEventRequest(titleRaw, null, date, null, null);
        CalendarEvent event = new CalendarEvent(id, Title.of(titleRaw), null, date, EventType.OTHER, null);
        EventResponse expectedResponse = new EventResponse(id, titleRaw, "No Desc", date, typeRaw, "No Schedule Set", warnings);

        Mockito.when(eventRepository.save(any(CalendarEvent.class))).thenReturn(Optional.of(event));
        EventResponse result = eventService.create(eventRequestInput);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.title(), result.title());
        assertEquals(expectedResponse.description(), result.description());
        assertEquals(expectedResponse.date(), result.date());
        assertEquals(expectedResponse.type(), result.type());
        assertEquals(expectedResponse.schedule(), result.schedule());
        Assertions.assertFalse(result.warnings().isEmpty());
        assertEquals(expectedResponse.warnings(), result.warnings());
        Mockito.verify(eventRepository).save(any());
    }

    @DisplayName("When create() is called and the repository returns empty Optional, EventNotSavedException should be thrown")
    @Test
    void createWhenRepositoryReturnsEmptyShouldThrowEventNotSavedException() {
        String titleRaw = "Visit to the museum";
        LocalDateTime date = LocalDateTime.of(2026, 9, 13, 18, 0);
        String typeRaw = "APPOINTMENT";
        CreateEventRequest eventRequestInput = new CreateEventRequest(titleRaw, null, date, typeRaw, null);

        Mockito.when(eventRepository.save(any(CalendarEvent.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(EventNotSavedException.class, () -> {
            eventService.create(eventRequestInput);
        });
        Mockito.verify(eventRepository).save(any());
    }

    @Test
    @DisplayName("getAll should call repository and return mapped events")
    void getAllShouldCallRepository() {
        CalendarEvent event1 = new CalendarEvent(1L, Title.of("E1"), null, LocalDateTime.now(), EventType.OTHER, null);
        CalendarEvent event2 = new CalendarEvent(2L, Title.of("E2"), null, LocalDateTime.now(), EventType.OTHER, null);
        List<CalendarEvent> mockList = List.of(event1, event2);

        Mockito.when(eventRepository.findAll()).thenReturn(mockList);

        List<EventResponse> result = eventService.getAll();

        assertEquals(2, result.size());
        Mockito.verify(eventRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("getUpcomingEvents should call repository and return mapped events")
    void getUpcomingEventsShouldCallRepository() {
        int days = 3;
        CalendarEvent event1 = new CalendarEvent(3L, Title.of("E3"), null, LocalDateTime.now(), EventType.OTHER, null);
        CalendarEvent event2 = new CalendarEvent(4L, Title.of("E4"), null, LocalDateTime.now(), EventType.OTHER, null);
        List<CalendarEvent> mockList = List.of(event1, event2);

        Mockito.when(eventRepository.findUpcomingEvents(days)).thenReturn(mockList);

        List<EventResponse> result = eventService.getUpcomingEvents(days);

        assertEquals(2, result.size());
        Mockito.verify(eventRepository, Mockito.times(1)).findUpcomingEvents(days);
    }

    @Test
    @DisplayName("getById should call repository and return mapped event")
    void getByIdShouldCallRepository() {
        long id = 23L;
        CalendarEvent event = new CalendarEvent(23L, Title.of("E5"), null, LocalDateTime.now(), EventType.OTHER, null);
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        EventResponse result = eventService.getById(id);

        Assertions.assertAll("Verify all response fields",
                () -> assertEquals(event.getId(), result.id()),
                () -> assertEquals(event.getTitle().value(), result.title()),
                () -> assertEquals("No Desc", result.description()),
                () -> assertEquals("No Schedule Set", result.schedule())
        );
        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("getById should throw EventNotFoundException when the repository returns an empty optional")
    void getByIdWhenRepositoryReturnsEmptyOptionalShouldThrowEventNotFoundException() {
        Mockito.when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class, () -> {
            eventService.getById(2L);
        });
        Mockito.verify(eventRepository, Mockito.times(1)).findById(2L);
    }

    @DisplayName("When update() is called passing an UpdateEventRequest with all values, the method should return a ResponseEvent object with all the new values")
    @Test
    void updateShouldReturnResponseEventWithAllNewValues() {
        Long id = 583L;
        String titleRaw = "finish 4th sprint";
        String descriptionRaw = "should do all tests";
        LocalDateTime date = LocalDateTime.of(2025, 5, 23, 9, 0);
        String typeRaw = "REMINDER";
        String scheduleRaw = "MONTHLY";
        List<String> warnings = List.of("Note: this event date is in the past");
        UpdateEventRequest eventRequestInput = new UpdateEventRequest(id, titleRaw, descriptionRaw, date, typeRaw, scheduleRaw);
        CalendarEvent event = new CalendarEvent(id, Title.of("finish 5th sprint"), Description.of("AnotherDescription"), date, EventType.BIRTHDATE, EventSchedule.YEARLY);
        EventResponse expectedResponse = new EventResponse(id, "Finish 4th Sprint", "Should do all tests", date, typeRaw, scheduleRaw, warnings);

        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        EventResponse result = eventService.update(eventRequestInput);

        Assertions.assertAll("Verify all response fields",
                () -> assertEquals(expectedResponse.id(), result.id()),
                () -> assertEquals(expectedResponse.title(), result.title()),
                () -> assertEquals(expectedResponse.description(), result.description()),
                () -> assertEquals(expectedResponse.schedule(), result.schedule()),
                () -> assertEquals(expectedResponse.date(), result.date()),
                () -> assertEquals(expectedResponse.type(), result.type()),
                () -> assertEquals(expectedResponse.schedule(), result.schedule()),
                () -> assertEquals(expectedResponse.warnings(), result.warnings()),
                () -> assertEquals(expectedResponse.warnings(), result.warnings())
        );

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        Mockito.verify(eventRepository, Mockito.times(1)).updateById(any());
    }

    @DisplayName("When update() is called passing an UpdateEventRequest with some new values, the method should return a ResponseEvent object with old and new values")
    @Test
    void updateShouldReturnResponseEventWithTheNewPassedValues() {
        Long id = 32L;
        String oldTitleRaw = "Old Title";
        String newDescriptionRaw = "New description";
        LocalDateTime oldDate = LocalDateTime.of(2025, 5, 23, 9, 0);
        String newTypeRaw = "OTHER";
        String oldScheduleRaw = "YEARLY";
        UpdateEventRequest eventRequestInput = new UpdateEventRequest(id, null, newDescriptionRaw, null, newTypeRaw, null);
        CalendarEvent event = new CalendarEvent(id, Title.of(oldTitleRaw), Description.of(newDescriptionRaw), oldDate, EventType.valueOf(newTypeRaw), EventSchedule.valueOf(oldScheduleRaw));
        EventResponse expectedResponse = new EventResponse(id, oldTitleRaw, newDescriptionRaw, oldDate, newTypeRaw, oldScheduleRaw, List.of());

        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        EventResponse result = eventService.update(eventRequestInput);


        Assertions.assertAll("Verify all response fields",
                () -> assertEquals(expectedResponse.id(), result.id()),
                () -> assertEquals(expectedResponse.title(), result.title()),
                () -> assertEquals(expectedResponse.description(), result.description()),
                () -> assertEquals(expectedResponse.schedule(), result.schedule()),
                () -> assertEquals(expectedResponse.date(), result.date()),
                () -> assertEquals(expectedResponse.type(), result.type()),
                () -> assertEquals(expectedResponse.schedule(), result.schedule())
        );

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        Mockito.verify(eventRepository, Mockito.times(1)).updateById(any());
    }

    @DisplayName("When update() is called and findById returns EmptyOptional, the method should throw an EventNotFoundException")
    @Test
    void updateShouldThrowEventNotFoundExceptionWhenRepositoryReturnsEmptyOptional() {
        long id = 32L;
        String titleRaw = "Title";
        String descriptionRaw = "Description";
        LocalDateTime date = LocalDateTime.of(2026, 3, 3, 19, 0);
        String typeRaw = "OTHER";
        String scheduleRaw = "DAILY";
        CalendarEvent event = new CalendarEvent(id, Title.of(titleRaw), Description.of(descriptionRaw), date, EventType.valueOf(typeRaw), EventSchedule.valueOf(scheduleRaw));
        EventResponse expectedResponse = new EventResponse(id, titleRaw, descriptionRaw, date, typeRaw, scheduleRaw, List.of());

        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        EventResponse result = eventService.delete(id);


        Assertions.assertAll("Verify all response fields",
                () -> assertEquals(expectedResponse.id(), result.id()),
                () -> assertEquals(expectedResponse.title(), result.title()),
                () -> assertEquals(expectedResponse.description(), result.description()),
                () -> assertEquals(expectedResponse.schedule(), result.schedule()),
                () -> assertEquals(expectedResponse.date(), result.date()),
                () -> assertEquals(expectedResponse.type(), result.type()),
                () -> assertEquals(expectedResponse.schedule(), result.schedule())
        );

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(id);
    }

    @DisplayName("When delete() is called and findById returns an event, the method should return an EventResponse with the values of the event found")
    @Test
    void deleteShouldReturnEventResponse() {
        long id = 35L;

        UpdateEventRequest eventRequestInput = new UpdateEventRequest(id, "E6", null, LocalDateTime.now(), "OTHER", null);

        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class, () -> {
            eventService.delete(eventRequestInput.id());
        });

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        Mockito.verify(eventRepository, Mockito.times(0)).deleteById(id);
    }

    @DisplayName("When delete() is called and findById returns EmptyOptional, the method should throw an EventNotFoundException")
    @Test
    void deleteShouldThrowEventNotFoundExceptionWhenRepositoryReturnsEmptyOptional() {
        long id = 35L;

        UpdateEventRequest eventRequestInput = new UpdateEventRequest(id, "E6", null, LocalDateTime.now(), "OTHER", null);

        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class, () -> {
            eventService.delete(eventRequestInput.id());
        });

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        Mockito.verify(eventRepository, Mockito.times(0)).deleteById(id);
    }
}
