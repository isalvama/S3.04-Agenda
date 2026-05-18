package org.agenda.note.service.strategy;

import org.agenda.note.dto.NoteResponse;

import java.util.List;

public interface NoteStrategy {
    List<NoteResponse> execute();
}
