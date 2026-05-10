package org.agenda.event.repository;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(int id) {
        super("event with id " + id + "does not exist");
    }
}
