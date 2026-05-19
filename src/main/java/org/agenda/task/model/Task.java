package org.agenda.task.model;

import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;
import java.util.Optional;

public class Task {

    private Long id;
    private Title title;
    private Description body;
    private Status status;
    private Priority priority;
    private LocalDateTime expirationDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long eventId;

    public Task(Long id, Title title, Description body, Status status, Priority priority, LocalDateTime expirationDate, LocalDateTime createdAt, LocalDateTime updatedAt, Long eventId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.status = (status != null) ? status : Status.PENDING;
        this.priority = priority;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.eventId = eventId;
    }

    public Task(Title title, Description body, Status status, Priority priority, LocalDateTime expirationDate, Long eventId) {
        this(null, title, body, status, priority, expirationDate, LocalDateTime.now(), LocalDateTime.now(), eventId);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title.value();
    }

    public String getBody() {
        return body != null ? body.value() : null;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<Priority> getPriority() {
        return Optional.ofNullable(priority);
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Optional<Long> getEventId() {
        return Optional.ofNullable(eventId);
    }

    public void setId(Long id) {
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalArgumentException(
                    String.format("ID is already set to %d and cannot be changed to %d.", this.id, id));
        }
        this.id = id;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public void setBody(Description body) {
        this.body = body;
    }

    public void setStatus(Status status) {
        this.status = (status != null) ? status : Status.PENDING;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}