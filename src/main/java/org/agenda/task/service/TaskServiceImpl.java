package org.agenda.task.service;

import org.agenda.task.dto.TaskRequest;
import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Status;
import org.agenda.task.model.Task;
import org.agenda.task.repository.TaskRepository;

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
        validateRequest(request);

        Task task = new Task(
                request.title(),
                request.body(),
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
                .orElseThrow(() -> new RuntimeException(String.format("Task with ID %d not found", id)));
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
        validateRequest(request);

        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Cannot update: Task with id %d not found", id)));

        task.setTitle(request.title());
        task.setBody(request.body());
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
                .orElseThrow(() -> new RuntimeException(String.format("Cannot complete: Task with id %d not found", id)));

        task.setStatus(Status.DONE);
        task.setUpdatedAt(LocalDateTime.now());

        return TaskResponse.fromEntity(repository.save(task));
    }

    @Override
    public boolean delete(Long id) {
        validateId(id);
        if (!repository.existsById(id)) {
            throw new RuntimeException(String.format("Cannot delete: Task with id %d not found", id));
        }
        return repository.deleteById(id);
    }

    private Long validateId(Long id) {
        return Objects.requireNonNull(id, "ID cannot be null");
    }

    private void validateRequest(TaskRequest request) {
        Objects.requireNonNull(request, "Task request cannot be null");
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("Task title is mandatory");
        }
    }
}