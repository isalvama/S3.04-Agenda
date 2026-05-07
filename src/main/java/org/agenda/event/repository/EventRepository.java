package org.agenda.event.repository;

import org.agenda.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    void save(Event event);
    Optional<Event> findById(int id);
    List<Event> findAll();
}