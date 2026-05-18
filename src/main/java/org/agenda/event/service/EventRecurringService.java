package org.agenda.event.service;

import org.agenda.event.model.CalendarEvent;
import org.agenda.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

public class EventRecurringService {
    private final EventRepository repository;

    public EventRecurringService(EventRepository repository) {
        this.repository = repository;
    }

    public void processRecurringEvents(){
        List<CalendarEvent> allEvents = repository.findAll();

        List<CalendarEvent> pastEventsWithSchedule = allEvents.stream().filter(e -> e.getDate().isBefore(LocalDateTime.now()) && e.getSchedule().isPresent()).toList();

        for (CalendarEvent event : pastEventsWithSchedule){
            CalendarEvent newEvent = event.createNextOccurrence();
            if (newEvent != null){
                repository.save(newEvent);
                event.setSchedule(null);
                repository.updateById(event);
                System.out.printf("Auto-scheduled next occurrence for Event #%s %s: %s", event.getId(), event.getTitle(), event.getDate());
            }
        }
    }
}
