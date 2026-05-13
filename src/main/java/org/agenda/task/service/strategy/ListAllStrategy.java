package org.agenda.task.service.strategy;

import org.agenda.task.dto.TaskResponse;
import org.agenda.task.repository.TaskRepository;

import java.util.List;

public class ListAllStrategy implements TaskStrategy {

    private final TaskRepository repository;

    public ListAllStrategy(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TaskResponse> execute() {
        return repository.findAll().stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
}