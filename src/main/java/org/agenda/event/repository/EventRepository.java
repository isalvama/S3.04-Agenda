package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    CalendarEvent save(CalendarEvent event);
    Optional<CalendarEvent> findById(int id);
    List<CalendarEvent> findAll();
    List<CalendarEvent> findUpcomingEvents(int intervalDays);

    boolean updateById(CalendarEvent event);
    boolean deleteById(int id);
}