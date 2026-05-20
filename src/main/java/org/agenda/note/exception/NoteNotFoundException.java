package org.agenda.note.exception;

import org.agenda.shared.domain.exception.DomainException;

public class NoteNotFoundException extends DomainException {
    public NoteNotFoundException(Long id) {
        super(String.format("Note with ID %d not found", id));
    }
}
