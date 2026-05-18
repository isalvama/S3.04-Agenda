package org.agenda.note.service;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.dto.NoteResponse;
import org.agenda.note.exception.NoteNotFoundException;
import org.agenda.note.model.Note;
import org.agenda.note.repository.NoteRepository;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.agenda.task.repository.TaskRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NoteServiceTest {

    private NoteRepository repository;
    private TaskRepositoryImpl taskRepository;
    private NoteService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(NoteRepository.class);
        taskRepository = Mockito.mock(TaskRepositoryImpl.class);
        service = new NoteServiceImpl(repository, taskRepository);
    }

    private Note createSampleNote() {
        Note note = new Note(
                Title.of("Meeting notes"),
                Description.of("Discussed project roadmap"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );
        note.setId(1L);
        return note;
    }

    private NoteRequest createValidRequest() {
        return new NoteRequest("Meeting notes", "Discussed project roadmap", 1L);
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPath {

        @Test
        @DisplayName("create: valid request returns NoteResponse with id")
        void createNoteWithValidRequest() {
            Note savedNote = createSampleNote();
            when(taskRepository.existsById(1L)).thenReturn(true);
            when(repository.save(any(Note.class))).thenReturn(savedNote);

            NoteResponse response = service.create(createValidRequest());

            assertAll("Note Response Validation",
                    () -> assertNotNull(response),
                    () -> assertEquals(1L, response.id()),
                    () -> assertEquals("Meeting Notes", response.title().value())
            );
            verify(taskRepository, times(1)).existsById(1L);
            verify(repository, times(1)).save(any(Note.class));
        }

        @Test
        @DisplayName("getById: existing id returns NoteResponse")
        void getByIdReturnsNote() {
            Note note = createSampleNote();
            when(repository.findById(1L)).thenReturn(Optional.of(note));

            NoteResponse response = service.getById(1L);

            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("Meeting Notes", response.title().value());
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("getAll: returns list of all notes")
        void getAllReturnsList() {
            Note note1 = createSampleNote();
            Note note2 = new Note(
                    Title.of("Second note"),
                    Description.of("Some body"),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    1L
            );
            note2.setId(2L);
            when(repository.findAll()).thenReturn(List.of(note1, note2));

            List<NoteResponse> result = service.getAll();

            assertEquals(2, result.size());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("getAllByTaskId: returns notes belonging to the given task")
        void getAllByTaskIdReturnsNotes() {
            Note note = createSampleNote();
            when(repository.findAllByTaskId(1L)).thenReturn(List.of(note));

            List<NoteResponse> result = service.getAllByTaskId(1L);

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).taskId());
            verify(repository, times(1)).findAllByTaskId(1L);
        }

        @Test
        @DisplayName("update: valid request updates and returns NoteResponse")
        void updateNoteWithValidRequest() {
            Note existingNote = createSampleNote();
            when(repository.findById(1L)).thenReturn(Optional.of(existingNote));
            when(taskRepository.existsById(1L)).thenReturn(true);
            when(repository.save(any(Note.class))).thenReturn(existingNote);

            NoteRequest updatedRequest = new NoteRequest("Updated title", "Updated body", 1L);

            NoteResponse response = service.update(1L, updatedRequest);

            assertNotNull(response);
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(any(Note.class));
        }

        @Test
        @DisplayName("delete: existing note returns true")
        void deleteExistingNote() {
            when(repository.existsById(1L)).thenReturn(true);
            when(repository.deleteById(1L)).thenReturn(true);

            boolean result = service.delete(1L);

            assertTrue(result);
            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Unhappy Path Tests")
    class UnhappyPath {

        @Test
        @DisplayName("create: null request throws NullPointerException")
        void createWithNullRequestThrows() {
            assertThrows(NullPointerException.class, () -> service.create(null));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("create: null title throws IllegalArgumentException")
        void createWithNullTitleThrows() {
            NoteRequest request = new NoteRequest(null, "body", 1L);

            assertThrows(IllegalArgumentException.class, () -> service.create(request));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("create: blank title throws IllegalArgumentException")
        void createWithBlankTitleThrows() {
            NoteRequest request = new NoteRequest("  ", "body", 1L);

            assertThrows(IllegalArgumentException.class, () -> service.create(request));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("create: non-existent taskId throws RuntimeException")
        void createWithNonExistentTaskIdThrows() {
            when(taskRepository.existsById(99L)).thenReturn(false);
            NoteRequest request = new NoteRequest("Title", "body", 99L);

            assertThrows(RuntimeException.class, () -> service.create(request));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("getById: null id throws NullPointerException")
        void getByIdWithNullIdThrows() {
            assertThrows(NullPointerException.class, () -> service.getById(null));
            verify(repository, never()).findById(any());
        }

        @Test
        @DisplayName("getById: non-existent id throws NoteNotFoundException")
        void getByIdNotFoundThrows() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            NoteNotFoundException ex = assertThrows(NoteNotFoundException.class, () -> service.getById(99L));
            assertTrue(ex.getMessage().contains("99"));
            verify(repository, times(1)).findById(99L);
        }

        @Test
        @DisplayName("getAllByTaskId: null id throws NullPointerException")
        void getAllByTaskIdWithNullIdThrows() {
            assertThrows(NullPointerException.class, () -> service.getAllByTaskId(null));
            verify(repository, never()).findAllByTaskId(any());
        }

        @Test
        @DisplayName("update: non-existent id throws NoteNotFoundException")
        void updateNotFoundThrows() {
            when(taskRepository.existsById(1L)).thenReturn(true);
            when(repository.findById(99L)).thenReturn(Optional.empty());

            NoteRequest request = createValidRequest();
            assertThrows(NoteNotFoundException.class, () -> service.update(99L, request));
            verify(repository, times(1)).findById(99L);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("update: non-existent taskId throws RuntimeException")
        void updateWithNonExistentTaskIdThrows() {
            when(taskRepository.existsById(99L)).thenReturn(false);
            NoteRequest request = new NoteRequest("Title", "body", 99L);

            assertThrows(RuntimeException.class, () -> service.update(1L, request));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("delete: null id throws NullPointerException")
        void deleteWithNullIdThrows() {
            assertThrows(NullPointerException.class, () -> service.delete(null));
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("delete: non-existent id throws NoteNotFoundException")
        void deleteNotFoundThrows() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThrows(NoteNotFoundException.class, () -> service.delete(99L));
            verify(repository, times(1)).existsById(99L);
            verify(repository, never()).deleteById(any());
        }
    }
}
