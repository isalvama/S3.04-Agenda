package com.agenda.task.service;

import com.agenda.task.model.Task;

import java.util.List;

public interface TaskService {
    void createTask(Task task);
    List<Task> getAllTasks();
}