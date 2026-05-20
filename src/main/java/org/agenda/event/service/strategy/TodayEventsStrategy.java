package org.agenda.event.service.strategy;

import org.agenda.event.dto.EventResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TodayEventsStrategy implements ListEventsStrategy{
    @Override
    public List<EventResponse> execute(List<EventResponse> allEvents) {
        List<EventResponse> filteredEvents = new ArrayList<>(allEvents.stream().filter(e -> e.date().toLocalDate().isEqual(LocalDate.now())).toList());
        filteredEvents.sort(Comparator.comparing(EventResponse::date));
        return filteredEvents;
    }
}
