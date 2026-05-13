package org.agenda.note.model;

import java.time.LocalDateTime;

public class Note {
    private Long id;
    private String title;
    private String body;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long taskId;

    public Note(Long id, String title, String body, LocalDateTime createdAt, LocalDateTime updatedAt, Long taskId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.taskId = taskId;
    }
    public Note(String title, String body, LocalDateTime createdAt, LocalDateTime updatedAt, Long taskId){
        this(null, title, body, LocalDateTime.now(), LocalDateTime.now(), taskId );
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getBody() { return body; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Long getTaskId() { return taskId; }

    public void setId(Long id) {
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalArgumentException(
                    String.format("ID is already set to %d and cannot be changed to %d.", this.id, id));
        }
        this.id = id;
    }

    public void setTitle(String title) { this.title = title; }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setTaskId(Long taskId) { this.taskId = taskId; }
}
