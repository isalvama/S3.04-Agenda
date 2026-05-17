package org.agenda.event.repository;

import org.agenda.shared.exception.DataAccessException;

public class EventNotUpdatedException extends DataAccessException {
    public EventNotUpdatedException(String message) {
        super(message);
    }
}
