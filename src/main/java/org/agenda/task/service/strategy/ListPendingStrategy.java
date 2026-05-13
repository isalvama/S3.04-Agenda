package org.agenda.task.service.strategy;

import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Status;
import org.agenda.task.repository.TaskRepository;

import java.util.List;

public class ListPendingStrategy implements TaskStrategy {

    private final TaskRepository repository;

    public ListPendingStrategy(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TaskResponse> execute() {
        return repository.findAllByStatus(Status.PENDING).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
}