package org.agenda.event.service;

import org.agenda.event.dto.EventRequest;
import org.agenda.event.dto.EventResponse;

import java.sql.SQLException;

public interface EventService {
    EventResponse createEvent(EventRequest eventRequest);
}
