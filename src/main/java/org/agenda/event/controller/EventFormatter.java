package org.agenda.event.controller;

import org.agenda.event.dto.EventResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class EventFormatter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String formatEventResponse(EventResponse eventResponse) {

        String warnings = eventResponse.warnings().isEmpty()
                ? "No warnings"
                : String.join(", ", eventResponse.warnings());

        return String.format("Id: %s | Title: %s | Description: %s | Date: %s | Type: %s | Schedule: %s. %s\n",
                eventResponse.id(),
                eventResponse.title(),
                eventResponse.description(),
                eventResponse.date().format(DATE_FORMAT),
                eventResponse.type(),
                eventResponse.schedule(),
                warnings
        );
    }

    public String formatEventResponses(List<EventResponse> eventResponses) {
        if (eventResponses == null || eventResponses.isEmpty()){return "No events to display\n";}
        StringBuilder response = new StringBuilder();
        for (EventResponse eventResponse : eventResponses) {
            response.append(formatEventResponse(eventResponse)
            );
        }
        return response.toString();
    }
}
