package org.agenda.task.exception;

import org.agenda.shared.domain.exception.DomainException;

public class TaskValidationException extends DomainException {
    public TaskValidationException(String message) {
        super(message);
    }
}