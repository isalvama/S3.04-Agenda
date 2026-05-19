package org.agenda.event.controller;

import org.agenda.event.dto.EventResponse;
import org.agenda.event.service.EventService;
import org.agenda.event.service.strategy.*;
import org.agenda.shared.domain.exception.DomainException;
import org.agenda.shared.exception.DataAccessException;

import java.util.List;
import java.util.Objects;

public class EventNotifier {
    EventService eventService;
    ListEventsStrategy lastTwoDaysEventsStrategy;
    ListEventsStrategy todayEventsStrategy;
    ListEventsStrategy nextSevenDaysEventsStrategy;
    EventFormatter eventFormatter;


    public EventNotifier(EventService eventService){
        this.eventService = Objects.requireNonNull(eventService);
        this.lastTwoDaysEventsStrategy = new LastTwoDaysEventsStrategy();
        this.todayEventsStrategy = new TodayEventsStrategy();
        this.nextSevenDaysEventsStrategy = new NextSevenDaysEventsStrategy();
        this.eventFormatter = new EventFormatter();

    }

    private List<EventResponse> fetchAllEvents() {
        try {
            return eventService.getAll();
        } catch (DomainException | DataAccessException e) {
            System.out.println("Error Fetching Events: " + e.getMessage());
            return List.of();
        }
    }

    public void notifyAllEvents (){
        List<EventResponse> allEvents = fetchAllEvents();
        if (allEvents.isEmpty()){
            System.out.println("No events found");
            return;
        }
        listPastEvents(allEvents);
        listTodayEvents(allEvents);
        listNextSevenDaysEvents(allEvents);
    }

    private void listPastEvents(List<EventResponse> allEvents){
        printSection("Last Two Days Events", lastTwoDaysEventsStrategy.execute(allEvents));
    }

    private void listTodayEvents(List<EventResponse> allEvents){
        printSection("Today Events", todayEventsStrategy.execute(allEvents));

    }

    private void listNextSevenDaysEvents(List<EventResponse> allEvents){
        printSection("Next Seven Days Events", nextSevenDaysEventsStrategy.execute(allEvents));
    }

    private void printSection(String title, List<EventResponse> eventResponsesToPrint){
        System.out.printf("------ %s ------\n", title);
        if (eventResponsesToPrint.isEmpty()){
            System.out.println("No events during this time period");
        } else {
            System.out.printf("%s\n", eventFormatter.formatEventResponses(eventResponsesToPrint));
        }
    }
}
