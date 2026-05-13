package org.agenda.note.service;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.dto.NoteResponse;
import org.agenda.note.model.Note;
import org.agenda.note.repository.NoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class NoteServiceImpl implements NoteService {
    private final NoteRepository repository;

    public NoteServiceImpl(NoteRepository repository){
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    @Override
    public NoteResponse create(NoteRequest request) {
        validateRequest(request);

        Note note = new Note(
                request.title(),
                request.body(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                request.taskId());

        Note savedNote = repository.save(note);
        return NoteResponse.fromEntity(savedNote);
    }

    @Override
    public NoteResponse getById(Long id) {
        return repository.findById(validateId(id))
                .map(NoteResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException(String.format("Note with ID %d not found", id)));
    }

    @Override
    public List<NoteResponse> getAll() {
        return repository.findAll().stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }

    @Override
    public List<NoteResponse> getAllByTaskId(Long taskId) {
        return repository.findById(validateId(taskId)).stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }

    @Override
    public NoteResponse update(Long id, NoteRequest request) {
        validateId(id);
        validateRequest(request);

        Note note = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Cannot update: Note with id %d not found", id)));

        note.setTitle(request.title());
        note.setBody(request.body());
        note.setTaskId(request.taskId());
        note.setUpdatedAt(LocalDateTime.now());

        return NoteResponse.fromEntity(repository.save(note));

    }

    @Override
    public boolean delete(Long id) {
        validateId(id);
        if(!repository.existsById(id)){
            throw new RuntimeException(String.format("Cannot delete: Note with id %d not found", id));
        }

        return repository.deleteById(id);
    }

    private Long validateId(Long id) {
        return Objects.requireNonNull(id, "ID cannot be null");
    }

    private void validateRequest(NoteRequest request) {
        Objects.requireNonNull(request, "Note request cannot be null");
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("Note title is mandatory");
        }
    }
}
