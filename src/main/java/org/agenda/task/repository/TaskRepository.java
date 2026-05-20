package org.agenda.task.repository;

import org.agenda.task.model.Status;
import org.agenda.task.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findAll();

    List<Task> findAllByStatus(Status status);

    boolean deleteById(Long id);

    boolean existsById(Long id);
}