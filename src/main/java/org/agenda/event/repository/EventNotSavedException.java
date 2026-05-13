package org.agenda.event.repository;

public class EventNotSavedException extends RuntimeException {
    public EventNotSavedException(String message) {
        super(message);
    }
}
