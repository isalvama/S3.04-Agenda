package org.agenda.event.service.strategy;

import org.agenda.event.dto.EventResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NextSevenDaysEventsStrategy implements ListEventsStrategy{

    @Override
    public List<EventResponse> execute(List<EventResponse> eventsToFilter) {
        List<EventResponse> filteredEvents = new ArrayList<>(eventsToFilter.stream().filter(e -> e.date().toLocalDate().isAfter(LocalDate.now()) && e.date().toLocalDate().isBefore(LocalDate.now().plusDays(8))).toList());
        filteredEvents.sort(Comparator.comparing(EventResponse::date));
        return filteredEvents;
    }
}
