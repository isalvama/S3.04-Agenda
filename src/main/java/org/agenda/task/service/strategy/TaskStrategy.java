package org.agenda.task.service.strategy;

import org.agenda.task.model.Task;

public interface TaskStrategy {
    boolean isValid(Task task);
}