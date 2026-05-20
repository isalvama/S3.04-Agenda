package org.agenda.note.dto;

public record NoteRequest(
        String title,
        String body,
        Long taskId
) { }