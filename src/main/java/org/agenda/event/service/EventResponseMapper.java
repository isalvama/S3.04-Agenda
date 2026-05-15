package org.agenda.event.service;

import org.agenda.event.dto.EventResponse;
import org.agenda.event.model.CalendarEvent;
import org.agenda.shared.domain.value_object.Description;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventResponseMapper {
    public EventResponse toResponseModel(CalendarEvent event, List<String> warningsService) {
        List<String> warnings = new ArrayList<>(warningsService);

        return new EventResponse(
                event.getId(),
                event.getTitle().value(),
                event.getDescription().map(Description::value).orElse("No Desc"),
                event.getDate(),
                event.getType().name(), //TODO Override toString (check documentation ab overriding toString in enums)
                event.getSchedule().map(Enum::name).orElse("No Schedule Set"),
                warnings
        );
    }

    public EventResponse toResponseModel(CalendarEvent event) {
        return new EventResponse(
                event.getId(),
                event.getTitle().value(),
                event.getDescription().map(Description::value).orElse("No Desc"),
                event.getDate(),
                event.getType().name(),
                event.getSchedule().map(Enum::name).orElse("No Schedule Set"),
                Collections.emptyList()
        );
    }

    public List<EventResponse> toResponseModels(List<CalendarEvent> events, List<String> warnings) {
        List<EventResponse> response = new ArrayList<>();
        for (CalendarEvent event : events) {
            response.add(toResponseModel(event, warnings));
        }
        return response;
    }

    public List<EventResponse> toResponseModels(List<CalendarEvent> events) {
        List<EventResponse> response = new ArrayList<>();
        for (CalendarEvent event : events) {
            response.add(toResponseModel(event));
        }
        return response;
    }
}
