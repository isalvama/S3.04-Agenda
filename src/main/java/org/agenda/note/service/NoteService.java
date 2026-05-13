package org.agenda.note.service;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.dto.NoteResponse;

import java.util.List;

public interface NoteService {

    NoteResponse create(NoteRequest request);

    NoteResponse getById(Long id);

    List<NoteResponse> getAll();

    List<NoteResponse> getAllByTaskId (Long taskId);

    NoteResponse update(Long id, NoteRequest request);

    boolean delete(Long id);
}
