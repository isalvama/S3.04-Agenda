package cat.itacademy.s04.t02.n03.agenda.note.repository;

import cat.itacademy.s04.t02.n03.agenda.note.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    void save(Note note);
    Optional<Note> findById(int id);
    List<Note> findAllByTaskId(int taskId);
}