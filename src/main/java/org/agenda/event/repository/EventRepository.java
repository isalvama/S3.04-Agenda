package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<CalendarEvent> save(CalendarEvent event);
    Optional<CalendarEvent> findById(long id);
    List<CalendarEvent> findAll();
    List<CalendarEvent> findUpcomingEvents(int intervalDays);

    void updateById(CalendarEvent event);
    boolean deleteById(long id);
}