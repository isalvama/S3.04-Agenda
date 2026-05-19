package org.agenda.event.service;

import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRecurringServiceTest {
    private EventRepository eventRepository;
    private EventRecurringService eventRecurringService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventRecurringService = new EventRecurringService(eventRepository);
    }

    @Test
    void shouldSaveAndSetScheduleToNull() {

        CalendarEvent pastEvent = mock(CalendarEvent.class);
        CalendarEvent nextEvent = mock(CalendarEvent.class);

        when(pastEvent.getDate()).thenReturn(LocalDateTime.now().minusDays(2));
        when(pastEvent.getSchedule()).thenReturn(Optional.of(EventSchedule.DAILY));
        when(pastEvent.createNextOccurrence()).thenReturn(nextEvent);
        when(eventRepository.findAll()).thenReturn(List.of(pastEvent));

        eventRecurringService.processRecurringEvents();

        verify(eventRepository, times(1)).save(nextEvent);

        verify(pastEvent).setSchedule(null);

        verify(eventRepository).updateById(pastEvent);
    }

    @Test
    void shouldDoNothingWhenEventsAreInTheFuture() {
        CalendarEvent futureEvent = mock(CalendarEvent.class);

        when(futureEvent.getDate()).thenReturn(LocalDateTime.now().plusDays(1));
        when(eventRepository.findAll()).thenReturn(List.of(futureEvent));

        eventRecurringService.processRecurringEvents();

        verify(eventRepository, never()).save(any());
        verify(eventRepository, never()).updateById(any());
    }

    @Test
    void shouldDoNothingWhenEventsAreNotScheduled() {
        CalendarEvent futureEvent = mock(CalendarEvent.class);

        when(futureEvent.getDate()).thenReturn(LocalDateTime.now().minusMonths(11));
        when(futureEvent.getSchedule()).thenReturn(Optional.empty());
        when(eventRepository.findAll()).thenReturn(List.of(futureEvent));

        eventRecurringService.processRecurringEvents();

        verify(eventRepository, never()).save(any());
        verify(eventRepository, never()).updateById(any());
    }
}