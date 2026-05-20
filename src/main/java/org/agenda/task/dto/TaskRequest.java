package org.agenda.task.dto;

import org.agenda.task.model.Priority;
import org.agenda.task.model.Status;

import java.time.LocalDateTime;

public record TaskRequest(
        String title,
        String body,
        Status status,
        Priority priority,
        LocalDateTime expirationDate,
        Long eventId
) {
}