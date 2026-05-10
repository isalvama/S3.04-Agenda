package org.agenda.event.service;

import com.mysql.cj.xdevapi.Warning;
import org.agenda.event.dto.EventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventRepository;
import org.agenda.shared.domain.value_object.Title;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = requireNonNull(eventRepository);
    }


    @Override
    public EventResponse createEvent(EventRequest eventRequest) {
        List<String> warnings = new ArrayList<>();

        if (eventRequest.date().isBefore(LocalDateTime.now())) {
            warnings.add("Note: this event date is in the past");
        }

        CalendarEvent event = CalendarEvent.create(
                Title.of(eventRequest.title()),
                eventRequest.description(),
                eventRequest.date(),
                EventType.valueOf(eventRequest.type()),
                EventSchedule.valueOf(eventRequest.eventSchedule())
        );
        event = eventRepository.save(event);
        return new EventResponse(event.getId(), event.getTitle().value(), warnings);
    }

}
