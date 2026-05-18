package org.agenda.note.service.strategy;

import org.agenda.note.dto.NoteResponse;
import org.agenda.note.repository.NoteRepository;

import java.util.List;

public class GetAllByTaskIdStrategy implements NoteStrategy{
    private final NoteRepository repository;
    private final Long taskId;

    public GetAllByTaskIdStrategy(NoteRepository repository, Long taskId) {
        this.repository = repository;
        this.taskId = taskId;
    }

    @Override
    public List<NoteResponse> execute() {
        return repository.findAllByTaskId(taskId).stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }

}
