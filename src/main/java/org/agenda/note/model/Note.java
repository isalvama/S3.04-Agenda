package org.agenda.note.model;

import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;

public class Note {
    private Long id;
    private Title title;
    private Description body;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long taskId;

    public Note(Long id, Title title, Description body, LocalDateTime createdAt, LocalDateTime updatedAt, Long taskId) {
        if(taskId == null){
            throw new IllegalArgumentException("taskId is required");
        }
        this.id = id;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.taskId = taskId;
    }
    public Note(Title title, Description body, LocalDateTime createdAt, LocalDateTime updatedAt, Long taskId){
        this(null, title, body, LocalDateTime.now(), LocalDateTime.now(), taskId );
    }

    public Long getId() { return id; }

    public Title getTitle() { return title; }

    public Description getBody() { return body; }

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

    public void setTitle(Title title) { this.title = title; }

    public void setBody(Description body) {
        this.body = body;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setTaskId(Long taskId) { this.taskId = taskId; }
}
