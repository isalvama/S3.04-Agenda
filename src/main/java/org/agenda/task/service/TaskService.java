package org.agenda.task.service;

import org.agenda.task.dto.TaskRequest;
import org.agenda.task.dto.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse create(TaskRequest request);

    TaskResponse getById(Long id);

    List<TaskResponse> getAll();

    List<TaskResponse> getPendingTasks();

    List<TaskResponse> getCompletedTasks();

    TaskResponse update(Long id, TaskRequest request);

    TaskResponse markAsDone(Long id);

    boolean delete(Long id);
}