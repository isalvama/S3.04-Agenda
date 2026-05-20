package org.agenda.shared.domain.exception;

public class InvalidDescriptionException extends DomainException {
    public InvalidDescriptionException(String message) {
        super("Invalid Description: " + message);
    }
}
