package org.agenda.event.service;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.DeleteEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.CalendarEvent;

import java.util.List;

public interface EventService {
    EventResponse createEvent(CreateEventRequest createEventRequest);

    EventResponse updateEvent(UpdateEventRequest updateEventRequest);

    void deleteEvent(DeleteEventRequest deleteEventRequest);

    List<CalendarEvent> listEvents();
}
