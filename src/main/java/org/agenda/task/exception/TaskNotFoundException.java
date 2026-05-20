package org.agenda.task.exception;

import org.agenda.shared.domain.exception.DomainException;

public class TaskNotFoundException extends DomainException {
    public TaskNotFoundException(Long id) {
        super(String.format("Task with ID %d not found", id));
    }
}