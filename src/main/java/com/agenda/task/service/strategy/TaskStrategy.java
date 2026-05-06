package com.agenda.task.service.strategy;

import com.agenda.task.model.Task;

public interface TaskStrategy {
    boolean isValid(Task task);
}