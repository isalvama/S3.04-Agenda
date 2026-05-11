package org.agenda.task.service.strategy;

import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Status;
import org.agenda.task.repository.TaskRepository;

import java.util.List;

public class ListCompletedStrategy implements TaskStrategy {

    private final TaskRepository repository;

    public ListCompletedStrategy(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TaskResponse> execute() {
        return repository.findAllByStatus(Status.DONE).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
}
