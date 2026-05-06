package cat.itacademy.s04.t02.n03.agenda.task.service;

import cat.itacademy.s04.t02.n03.agenda.task.model.Task;

import java.util.List;

public interface TaskService {
    void createTask(Task task);
    List<Task> getAllTasks();
}