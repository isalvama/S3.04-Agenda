package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    CalendarEvent save(CalendarEvent event
            //, DataSource dataSource, String schema
              );
    Optional<CalendarEvent> findById(int id);
    List<CalendarEvent> findAll();
}