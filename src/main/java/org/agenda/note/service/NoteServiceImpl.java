package org.agenda.note.service;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.dto.NoteResponse;
import org.agenda.note.exception.NoteNotFoundException;
import org.agenda.note.model.Note;
import org.agenda.note.repository.NoteRepository;
import org.agenda.note.service.strategy.GetAllByTaskIdStrategy;
import org.agenda.note.service.strategy.GetAllNotesStrategy;
import org.agenda.note.service.strategy.NoteStrategy;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.agenda.task.repository.TaskRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class NoteServiceImpl implements NoteService {
    private final NoteRepository repository;
    private final TaskRepositoryImpl taskRepository;

    public NoteServiceImpl(NoteRepository repository, TaskRepositoryImpl taskRepository){
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.taskRepository = Objects.requireNonNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    public NoteResponse create(NoteRequest request) {
        validateRequest(request);

        if(!taskRepository.existsById(request.taskId())){
            throw new RuntimeException("Cannot create note: Task does not exist");
        }

        Note note = new Note(
                Title.of(request.title()),
                Description.of(request.body()),
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
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    public List<NoteResponse> getAll() {
        NoteStrategy strategy = new GetAllNotesStrategy(repository);
        return strategy.execute();
    }

    @Override
    public List<NoteResponse> getAllByTaskId(Long taskId) {
        validateId(taskId);
        NoteStrategy strategy = new GetAllByTaskIdStrategy(repository, taskId);
        return strategy.execute();
    }

    @Override
    public NoteResponse update(Long id, NoteRequest request) {
        validateId(id);
        validateRequest(request);

        Note note = repository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        note.setTitle(Title.of(request.title()));
        note.setBody(Description.of(request.body()));
        note.setTaskId(request.taskId());
        note.setUpdatedAt(LocalDateTime.now());

        return NoteResponse.fromEntity(repository.save(note));
    }

    @Override
    public boolean delete(Long id) {
        validateId(id);
        if(!repository.existsById(id)){
            throw new NoteNotFoundException(id);
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
