package org.agenda.task.service.strategy;

import org.agenda.task.dto.TaskResponse;

import java.util.List;

public interface TaskStrategy {

    List<TaskResponse> execute();
}