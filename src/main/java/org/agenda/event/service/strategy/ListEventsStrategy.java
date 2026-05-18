package org.agenda.event.service.strategy;

import org.agenda.event.dto.EventResponse;
import org.agenda.event.model.CalendarEvent;

import java.util.List;

public interface ListEventsStrategy {
    List<EventResponse> execute (List<EventResponse> eventsToFilter);
}
