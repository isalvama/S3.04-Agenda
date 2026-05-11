package org.agenda.task.service.strategy;

import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Task;

import java.util.List;

public interface TaskStrategy {

    List<TaskResponse> execute();
}