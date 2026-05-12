package org.agenda.event.service;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;

import java.util.List;

public interface EventService {
    EventResponse create(CreateEventRequest createEventRequest);

    EventResponse update(UpdateEventRequest updateEventRequest);

    List<EventResponse> getAll();

    List<EventResponse> getUpcomingEvents(int intervalDays);

    EventResponse getById(long id);

    boolean delete(long id);
}
