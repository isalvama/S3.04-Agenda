package org.agenda.task.dto;

import org.agenda.task.model.Priority;
import org.agenda.task.model.Status;
import org.agenda.task.model.Task;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String body,
        Status status,
        Priority priority,
        LocalDateTime expirationDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long eventId
) {

    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getBody(),
                task.getStatus(),
                task.getPriority().orElse(null),
                task.getExpirationDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getEventId().orElse(null)
        );
    }
}
