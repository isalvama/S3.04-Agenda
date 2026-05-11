package org.agenda.event.service;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.CalendarEvent;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventResponse createEvent(CreateEventRequest createEventRequest);

    EventResponse updateEvent(UpdateEventRequest updateEventRequest);

    boolean deleteEvent(int id);

    List<EventResponse> listAllEvents();

    List<EventResponse> listUpcomingEvents(int intervalDays);
}
