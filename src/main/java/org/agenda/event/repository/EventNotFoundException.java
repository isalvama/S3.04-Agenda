package org.agenda.event.repository;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String failedPerformance, int id) {
        super(String.format("Failed %s: Event with id %s not found", failedPerformance, id));
    }
}
