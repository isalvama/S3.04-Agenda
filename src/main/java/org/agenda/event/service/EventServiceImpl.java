package org.agenda.event.service;

import org.agenda.event.dto.CreateEventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.dto.UpdateEventRequest;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventNotFoundException;
import org.agenda.event.repository.EventNotSavedException;
import org.agenda.event.repository.EventRepository;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventResponseMapper eventResponseMapper = new EventResponseMapper();

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = requireNonNull(eventRepository);
    }


    @Override
    public EventResponse create(CreateEventRequest eventRequest) {

        CalendarEvent event = CalendarEvent.create(
                Title.of(eventRequest.title()),
                eventRequest.description() != null ? Description.of(eventRequest.description()) : null,
                eventRequest.date(),
                eventRequest.type() != null ? EventType.valueOf(eventRequest.type().toUpperCase()) : EventType.OTHER,
                eventRequest.eventSchedule() != null ? EventSchedule.valueOf(eventRequest.eventSchedule().toUpperCase()) : null
        );

        CalendarEvent response = eventRepository.save(event).
                orElseThrow(() -> new EventNotSavedException(String.format("Failed Save: Event \n%s\n could not be saved", event.getTitle())));

        List<String> warnings = new ArrayList<>();
        event.checkIfDateIsInThePast(LocalDateTime.now()).ifPresent(warnings::add);

        return eventResponseMapper.toResponseModel(response, warnings);
    }

    @Override
    public List<EventResponse> getAll() {
        return eventResponseMapper.toResponseModels(eventRepository.findAll());
    }

    @Override
    public List<EventResponse> getUpcomingEvents(int intervalDays) {
        return eventResponseMapper.toResponseModels(eventRepository.findUpcomingEvents(intervalDays));
    }

    @Override
    public EventResponse getById(long id) {
        return eventRepository.findById(id).map(eventResponseMapper::toResponseModel).
                orElseThrow(() -> new EventNotFoundException("Search", id));
    }


    @Override
    public EventResponse update(UpdateEventRequest eventRequest) {
        final CalendarEvent event = eventRepository.findById(eventRequest.id())
                .orElseThrow(() -> new EventNotFoundException("Update", eventRequest.id()));

        Optional.ofNullable(eventRequest.title()).ifPresent(title -> event.setTitle(Title.of(title)));
        Optional.ofNullable(eventRequest.description()).ifPresent(desc -> event.setDescription(Description.of(desc)));

        List<String> warnings = new ArrayList<>();
        if (eventRequest.date() != null) {
            event.setDate(eventRequest.date());
            event.checkIfDateIsInThePast(LocalDateTime.now()).ifPresent(warnings::add);
        }

        Optional.ofNullable(eventRequest.type()).ifPresent(type -> event.setType(EventType.valueOf(type)));
        Optional.ofNullable(eventRequest.eventSchedule()).ifPresent(schedule -> event.setSchedule(EventSchedule.valueOf(schedule)));

        eventRepository.updateById(event);

        return eventResponseMapper.toResponseModel(event, warnings);
    }

    @Override
    public EventResponse delete(long id) {
        final CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Delete", id));

        eventRepository.deleteById(id);

        return eventResponseMapper.toResponseModel(event);
    }


}