package cat.itacademy.s04.t02.n03.agenda.task.service.strategy;

import cat.itacademy.s04.t02.n03.agenda.task.model.Task;

public interface TaskStrategy {
    boolean isValid(Task task);
}