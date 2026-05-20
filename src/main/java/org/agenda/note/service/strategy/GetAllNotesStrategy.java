package org.agenda.note.service.strategy;

import org.agenda.note.dto.NoteResponse;
import org.agenda.note.repository.NoteRepository;

import java.util.List;

public class GetAllNotesStrategy implements NoteStrategy{
    private final NoteRepository repository;

    public GetAllNotesStrategy(NoteRepository repository){
        this.repository = repository;
    }

    @Override
    public List<NoteResponse> execute() {
        return repository.findAll().stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }
}
