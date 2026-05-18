package org.agenda.note.dto;

import org.agenda.note.model.Note;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;

public record NoteResponse (
        Long id,
        Title title,
        Description body,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long taskId
){
    public static NoteResponse fromEntity(Note note){
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getBody(),
                note.getCreatedAt(),
                note.getUpdatedAt(),
                note.getTaskId()
        );
    }
}
