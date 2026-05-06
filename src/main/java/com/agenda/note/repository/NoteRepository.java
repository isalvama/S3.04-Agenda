package com.agenda.note.repository;

import com.agenda.note.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    void save(Note note);
    Optional<Note> findById(int id);
    List<Note> findAllByTaskId(int taskId);
}