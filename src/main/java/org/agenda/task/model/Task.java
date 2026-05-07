package org.agenda.task.model;

import java.time.LocalDateTime;
import java.util.Optional;

public class Task {

    private Long id;
    private String title;
    private String body;
    private Status status;
    private Priority priority;
    private LocalDateTime expirationDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long eventId;

    public Task(Long id, String title, String body, Status status, Priority priority, LocalDateTime expirationDate, LocalDateTime createdAt, LocalDateTime updatedAt, Long eventId) {
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

    public Task(String title, String body, Status status, Priority priority, LocalDateTime expirationDate, Long eventId) {
        this(null, title, body, status, priority, expirationDate, LocalDateTime.now(), LocalDateTime.now(), eventId);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
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