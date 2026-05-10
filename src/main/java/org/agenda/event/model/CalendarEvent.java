package org.agenda.event.model;

import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class CalendarEvent {
    private int id;
    private Title title;
    private Description description;
    private LocalDateTime date;
    private EventType type;
    private EventSchedule schedule;

    public CalendarEvent(int id, Title title, String description, LocalDateTime date, EventType type, EventSchedule schedule) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "event title can not be null");
        this.description = (description != null && !description.isBlank()) ? Description.of(description) : null ;
        this.date = Objects.requireNonNull(date, "event date can not be null");
        this.type = Objects.requireNonNull(type, "event type can not be null");
        this.schedule = schedule;
    }

    public static CalendarEvent create(Title title, String description, LocalDateTime date, EventType type, EventSchedule schedule) {
        return new CalendarEvent(
                0,
                title,
                description,
                date,
                type,
                schedule
        );
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Optional<Description> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(String description) {
        this.description = (description != null && !description.isBlank()) ? Description.of(description) : null;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(EventSchedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", date=" + date +
                ", type=" + type +
                ", schedule=" + schedule +
                '}';
    }
}