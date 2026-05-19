package org.agenda.task.service;

import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.agenda.task.dto.TaskRequest;
import org.agenda.task.dto.TaskResponse;
import org.agenda.task.exception.TaskNotFoundException;
import org.agenda.task.model.Status;
import org.agenda.task.model.Task;
import org.agenda.task.repository.TaskRepository;
import org.agenda.task.service.strategy.TaskStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        Objects.requireNonNull(request, "Task request cannot be null");

        Task task = new Task(
                Title.of(request.title()),
                Description.of(request.body()),
                request.status(),
                request.priority(),
                request.expirationDate(),
                request.eventId());

        Task savedTask = repository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Override
    public TaskResponse getById(Long id) {
        return repository.findById(validateId(id))
                .map(TaskResponse::fromEntity)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public List<TaskResponse> getAll() {
        return repository.findAll().stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TaskResponse> getPendingTasks() {
        return repository.findAllByStatus(Status.PENDING).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TaskResponse> getCompletedTasks() {
        return repository.findAllByStatus(Status.DONE).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        validateId(id);
        Objects.requireNonNull(request, "Task request cannot be null");

        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(Title.of(request.title()));
        task.setBody(Description.of(request.body()));
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setExpirationDate(request.expirationDate());
        task.setEventId(request.eventId());
        task.setUpdatedAt(LocalDateTime.now());

        return TaskResponse.fromEntity(repository.save(task));
    }

    @Override
    public TaskResponse markAsDone(Long id) {
        Task task = repository.findById(validateId(id))
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setStatus(Status.DONE);
        task.setUpdatedAt(LocalDateTime.now());

        return TaskResponse.fromEntity(repository.save(task));
    }

    @Override
    public boolean delete(Long id) {
        validateId(id);
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        return repository.deleteById(id);
    }

    private Long validateId(Long id) {
        return Objects.requireNonNull(id, "ID cannot be null");
    }

    @Override
    public List<TaskResponse> listTasks(TaskStrategy strategy) {
        return strategy.execute();
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}