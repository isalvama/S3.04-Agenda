package org.agenda.note.controller;

import org.agenda.note.dto.NoteResponse;
import org.agenda.note.exception.NoteNotFoundException;
import org.agenda.note.service.NoteService;
import org.agenda.shared.console.ConsoleReader;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.agenda.task.dto.TaskResponse;
import org.agenda.task.model.Priority;
import org.agenda.task.model.Status;
import org.agenda.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NoteControllerTest {

    private NoteService noteService;
    private TaskService taskService;
    private NoteController controller;
    private ByteArrayOutputStream outputStream;
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        noteService = Mockito.mock(NoteService.class);
        taskService = Mockito.mock(TaskService.class);
        scanner = new Scanner(System.in);
        controller = new NoteController(noteService, taskService, scanner);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private void setInput(String input) {
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ConsoleReader.setScanner(scanner);
        controller = new NoteController(noteService, taskService, scanner);
    }

    private String output() {
        return outputStream.toString();
    }

    private NoteResponse sampleNoteResponse() {
        return new NoteResponse(
                1L,
                Title.of("Meeting Notes"),
                Description.of("Discussed project roadmap"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );
    }

    private TaskResponse sampleTaskResponse() {
        return new TaskResponse(1L, "Buy groceries", "Milk, eggs", Status.PENDING,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7), LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPath {

        @Test
        @DisplayName("create: valid input creates note and prints success message")
        void createNoteSuccessfully() {
            setInput("1\nMeeting notes\nDiscussed project roadmap\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(1L)).thenReturn(true);
            when(noteService.create(any())).thenReturn(sampleNoteResponse());

            controller.create();

            verify(noteService, times(1)).create(any());
            assertTrue(output().contains("Note created successfully"));
        }

        @Test
        @DisplayName("getById: existing id prints note details")
        void getByIdPrintsNote() {
            setInput("1\n");
            when(noteService.getById(1L)).thenReturn(sampleNoteResponse());

            controller.getById();

            verify(noteService, times(1)).getById(1L);
            assertTrue(output().contains("#1"));
        }

        @Test
        @DisplayName("getAll: returns and prints all notes")
        void getAllPrintsNotes() {
            when(noteService.getAll()).thenReturn(List.of(sampleNoteResponse()));

            controller.getAll();

            verify(noteService, times(1)).getAll();
            assertTrue(output().contains("#1"));
        }

        @Test
        @DisplayName("getAll: empty list prints 'No notes found'")
        void getAllEmptyListPrintsMessage() {
            when(noteService.getAll()).thenReturn(List.of());

            controller.getAll();

            assertTrue(output().contains("No notes found"));
        }

        @Test
        @DisplayName("getAllByTaskId: valid taskId prints notes for that task")
        void getAllByTaskIdPrintsNotes() {
            setInput("1\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(noteService.getAllByTaskId(1L)).thenReturn(List.of(sampleNoteResponse()));

            controller.getAllByTaskId();

            verify(noteService, times(1)).getAllByTaskId(1L);
            assertTrue(output().contains("#1"));
        }

        @Test
        @DisplayName("getAllByTaskId: empty result prints 'No notes found for this task'")
        void getAllByTaskIdEmptyPrintsMessage() {
            setInput("1\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(noteService.getAllByTaskId(1L)).thenReturn(List.of());

            controller.getAllByTaskId();

            assertTrue(output().contains("No notes found for this task"));
        }

        @Test
        @DisplayName("update: valid input updates note and prints success message")
        void updateNoteSuccessfully() {
            setInput("1\n1\nUpdated title\nUpdated body\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(1L)).thenReturn(true);
            when(noteService.update(eq(1L), any())).thenReturn(sampleNoteResponse());

            controller.update();

            verify(noteService, times(1)).update(eq(1L), any());
            assertTrue(output().contains("Note updated successfully"));
        }

        @Test
        @DisplayName("create: empty body creates note with null body")
        void createNoteWithNullBody() {
            setInput("1\nMeeting notes\n\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(1L)).thenReturn(true);
            when(noteService.create(any())).thenReturn(sampleNoteResponse());

            controller.create();

            verify(noteService, times(1)).create(any());
            assertTrue(output().contains("Note created successfully"));
        }

        @Test
        @DisplayName("update: empty body updates note with null body")
        void updateNoteWithNullBody() {
            setInput("1\n1\nUpdated title\n\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(1L)).thenReturn(true);
            when(noteService.update(eq(1L), any())).thenReturn(sampleNoteResponse());

            controller.update();

            verify(noteService, times(1)).update(eq(1L), any());
            assertTrue(output().contains("Note updated successfully"));
        }

        @Test
        @DisplayName("delete: existing note prints success message")
        void deleteNoteSuccessfully() {
            setInput("1\n");
            when(noteService.delete(1L)).thenReturn(true);

            controller.delete();

            verify(noteService, times(1)).delete(1L);
            assertTrue(output().contains("Note deleted successfully"));
        }

        @Test
        @DisplayName("delete: delete returns false prints could not be deleted message")
        void deleteReturnsFalsePrintsMessage() {
            setInput("1\n");
            when(noteService.delete(1L)).thenReturn(false);

            controller.delete();

            assertTrue(output().contains("Note could not be deleted"));
        }
    }

    @Nested
    @DisplayName("Unhappy Path Tests")
    class UnhappyPath {

        @Test
        @DisplayName("create: non-existent taskId prints error and returns without creating")
        void createWithNonExistentTaskIdPrintsError() {
            setInput("99\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(99L)).thenReturn(false);

            controller.create();

            verify(noteService, never()).create(any());
            assertTrue(output().contains("Task does not exist"));
        }

        @Test
        @DisplayName("create: service throws exception prints error message")
        void createServiceExceptionPrintsError() {
            setInput("1\nMeeting notes\nsome body\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(1L)).thenReturn(true);
            when(noteService.create(any())).thenThrow(new RuntimeException("Unexpected error"));

            controller.create();

            assertTrue(output().contains("Error creating note"));
        }

        @Test
        @DisplayName("getById: NoteNotFoundException prints 'Note not found'")
        void getByIdNotFoundPrintsMessage() {
            setInput("99\n");
            when(noteService.getById(99L)).thenThrow(new NoteNotFoundException(99L));

            controller.getById();

            assertTrue(output().contains("Note not found"));
        }

        @Test
        @DisplayName("getById: unexpected exception prints error message")
        void getByIdExceptionPrintsError() {
            setInput("1\n");
            when(noteService.getById(1L)).thenThrow(new RuntimeException("DB failure"));

            controller.getById();

            assertTrue(output().contains("Error fetching note"));
        }

        @Test
        @DisplayName("getAllByTaskId: service throws exception prints error message")
        void getAllByTaskIdExceptionPrintsError() {
            setInput("1\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(noteService.getAllByTaskId(1L)).thenThrow(new RuntimeException("error"));

            controller.getAllByTaskId();

            assertTrue(output().contains("Error fetching notes by task"));
        }

        @Test
        @DisplayName("update: non-existent taskId prints error and returns without updating")
        void updateWithNonExistentTaskIdPrintsError() {
            setInput("1\n99\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(99L)).thenReturn(false);

            controller.update();

            verify(noteService, never()).update(any(), any());
            assertTrue(output().contains("Task does not exist"));
        }

        @Test
        @DisplayName("update: NoteNotFoundException prints 'Note not found'")
        void updateNotFoundPrintsMessage() {
            setInput("99\n1\nUpdated title\nUpdated body\n");
            when(taskService.getAll()).thenReturn(List.of(sampleTaskResponse()));
            when(taskService.existsById(1L)).thenReturn(true);
            when(noteService.update(eq(99L), any())).thenThrow(new NoteNotFoundException(99L));

            controller.update();

            assertTrue(output().contains("Note not found"));
        }

        @Test
        @DisplayName("delete: NoteNotFoundException prints 'Note not found'")
        void deleteNotFoundPrintsMessage() {
            setInput("99\n");
            when(noteService.delete(99L)).thenThrow(new NoteNotFoundException(99L));

            controller.delete();

            assertTrue(output().contains("Note not found"));
        }

        @Test
        @DisplayName("delete: unexpected exception prints error message")
        void deleteExceptionPrintsError() {
            setInput("1\n");
            when(noteService.delete(1L)).thenThrow(new RuntimeException("DB failure"));

            controller.delete();

            assertTrue(output().contains("Error deleting note"));
        }
    }
}
