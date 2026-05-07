package org.agenda.task.service;

import org.agenda.task.model.Task;

import java.util.List;

public interface TaskService {
    void createTask(Task task);
    List<Task> getAllTasks();
}