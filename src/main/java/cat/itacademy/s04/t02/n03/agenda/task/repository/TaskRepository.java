package cat.itacademy.s04.t02.n03.agenda.task.repository;

import cat.itacademy.s04.t02.n03.agenda.task.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void save (Task task);
    Optional<Task> findById (int id);
    List<Task> findAll ();
}