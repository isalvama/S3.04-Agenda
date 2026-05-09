package org.agenda.event.service;

import org.agenda.event.dto.EventRequest;
import org.agenda.event.dto.EventResponse;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.event.repository.EventRepository;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = requireNonNull(eventRepository);
    }


    @Override
    public EventResponse createEvent(EventRequest eventRequest) {
        EventResponse response = new EventResponse(false, "", new ArrayList<>());

        if (eventRequest.date().isBefore(LocalDateTime.now())) {
            response.addWarnings("");
        }

        CalendarEvent event = CalendarEvent.create(
                Title.of(eventRequest.title()),
                eventRequest.description(),
                eventRequest.date(),
                EventType.valueOf(eventRequest.type()),
                EventSchedule.valueOf(eventRequest.eventSchedule())
        );

        //eventRepository.save(event);
        return response;
    }
        /**
         *         Artist artist = Artist.create(
         *                 promoterId,
         *                 Name.of(command.name()),
         *                 City.of(command.origin()),
         *                 genres,
         *                 imageAssetId,
         *                 Description.of(command.bio()),
         *                 ArtistStatus.valueOf(command.status()),
         *                 ArtistFee.of(command.fee()),
         *                 FollowerCount.of(command.followers()),
         *                 tags,
         *                 ArtistContact.of(command.contact()),
         *                 SocialLinks.of(command.instagramUrl(), command.spotifyUrl())
         *         );
         *
         *         return artistRepository.save(artist);
         *     }
         *
         *
         *
         *
         */
}
