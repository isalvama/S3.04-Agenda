package org.agenda.event.service;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.DeleteEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventNotFoundException;
import org.agenda.event.repository.EventRepository;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

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
    public EventResponse createEvent(CreateEventRequest eventRequest) {
        List<String> warnings = new ArrayList<>();

        if (eventRequest.date().isBefore(LocalDateTime.now())) {
            warnings.add("Note: this event date is in the past");
        }

        CalendarEvent event = CalendarEvent.create(
                Title.of(eventRequest.title()),
                Description.of(eventRequest.description()),
                eventRequest.date(),
                EventType.valueOf(eventRequest.type()),
                EventSchedule.valueOf(eventRequest.eventSchedule())
        );
        event = eventRepository.save(event);
        return new EventResponse(event.getId(), event.getTitle().value(), warnings);
    }

    @Override
    public void deleteEvent(DeleteEventRequest deleteEventRequest) {
        final CalendarEvent event = eventRepository.findById(deleteEventRequest.id())
                .orElseThrow(() -> new EventNotFoundException(deleteEventRequest.id()));

        eventRepository.delete(deleteEventRequest.id());

        new EventResponse(event.getId(), event.getTitle().value(), null);
    }

    @Override
    public List<CalendarEvent> listEvents() {
        return List.of();
    }

    @Override
    public EventResponse updateEvent(UpdateEventRequest eventRequest) {
        List<String> warnings = new ArrayList<>();
        final CalendarEvent event = eventRepository.findById(eventRequest.id())
                .orElseThrow(() -> new EventNotFoundException(eventRequest.id()));

        if (eventRequest.title() != null) {
            event.setTitle(eventRequest.title());
        }

        if (eventRequest.description() != null) {
            event.setDescription(eventRequest.description());
        }

        if (eventRequest.date() != null) {
            event.setDate(eventRequest.date());
            if (event.getDate().isBefore(LocalDateTime.now())) {
                warnings.add("Note: this event date is in the past");
            }
        }

        if (eventRequest.type() != null) {
            event.setType(EventType.valueOf(eventRequest.type()));
        }

        if (eventRequest.eventSchedule() != null) {
            event.setSchedule(EventSchedule.valueOf(eventRequest.eventSchedule()));
        }

        eventRepository.update(event);

        return new EventResponse(event.getId(), event.getTitle().value(), warnings); //TODO REVIEW
    }

}
