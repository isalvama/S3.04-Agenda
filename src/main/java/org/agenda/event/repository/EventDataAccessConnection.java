package org.agenda.event.repository;

import org.agenda.shared.domain.exception.DataAccessException;

public class EventDataAccessConnection extends DataAccessException {
    public EventDataAccessConnection(String message) {
        super(message);
    }
}
