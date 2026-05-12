package org.agenda.note.repository;

import org.agenda.note.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    Note save(Note note);
    Optional<Note> findById(Long id);
    List<Note> findAllByTaskId(Long taskId);
    List<Note> findAll();
    boolean deleteById(Long id);
    boolean existsById(Long id);
}