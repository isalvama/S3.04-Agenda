package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    void save(CalendarEvent event) throws SQLException;
    Optional<CalendarEvent> findById(int id);
    List<CalendarEvent> findAll();
}