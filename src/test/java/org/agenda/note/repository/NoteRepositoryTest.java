package org.agenda.note.repository;

import org.agenda.note.model.Note;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.agenda.shared.exception.AgendaException;
import org.agenda.shared.exception.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NoteRepositoryTest {

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private NoteRepositoryImpl repository;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Mockito.mock(Connection.class);
        ps = Mockito.mock(PreparedStatement.class);
        rs = Mockito.mock(ResultSet.class);
        repository = new NoteRepositoryImpl(connection);
    }

    private Note createSampleNote() {
        Note note = new Note(
                Title.of("Meeting Notes"),
                Description.of("Discussed project roadmap"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );
        note.setId(1L);
        return note;
    }

    private void mockResultSetRow(ResultSet rs, long id, String title, String body, long taskId) throws SQLException {
        when(rs.getLong("id")).thenReturn(id);
        when(rs.getString("title")).thenReturn(title);
        when(rs.getString("body")).thenReturn(body);
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getLong("task_id")).thenReturn(taskId);
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPath {

        @Test
        @DisplayName("save (insert): new note without id executes INSERT and returns note with generated id")
        void saveNewNoteInsertsAndReturnsWithId() throws SQLException {
            Note note = new Note(
                    Title.of("Meeting Notes"),
                    Description.of("Discussed project roadmap"),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    1L
            );
            ResultSet generatedKeys = Mockito.mock(ResultSet.class);
            when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
            when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
            when(generatedKeys.next()).thenReturn(true);
            when(generatedKeys.getLong(1)).thenReturn(10L);

            Note result = repository.save(note);

            assertNotNull(result);
            assertEquals(10L, result.getId());
            verify(ps, times(1)).executeUpdate();
        }

        @Test
        @DisplayName("save (update): note with id executes UPDATE and returns note")
        void saveExistingNoteUpdatesAndReturns() throws SQLException {
            Note note = createSampleNote();
            when(connection.prepareStatement(anyString())).thenReturn(ps);

            Note result = repository.save(note);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(ps, times(1)).executeUpdate();
        }

        @Test
        @DisplayName("findById: existing id returns Optional with note")
        void findByIdReturnsNote() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            mockResultSetRow(rs, 1L, "Meeting Notes", "Discussed project roadmap", 1L);

            Optional<Note> result = repository.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
            assertEquals("Meeting Notes", result.get().getTitle().value());
        }

        @Test
        @DisplayName("findById: non-existing id returns empty Optional")
        void findByIdReturnsEmpty() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            Optional<Note> result = repository.findById(99L);

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("findAll: returns list of all notes")
        void findAllReturnsList() throws SQLException {
            Statement st = Mockito.mock(Statement.class);
            when(connection.createStatement()).thenReturn(st);
            when(st.executeQuery(anyString())).thenReturn(rs);
            when(rs.next()).thenReturn(true, true, false);
            mockResultSetRow(rs, 1L, "Meeting Notes", "Body one", 1L);

            List<Note> result = repository.findAll();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("findAllByTaskId: returns notes for given taskId")
        void findAllByTaskIdReturnsList() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true, false);
            mockResultSetRow(rs, 1L, "Meeting Notes", "Discussed project roadmap", 1L);

            List<Note> result = repository.findAllByTaskId(1L);

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getTaskId());
        }

        @Test
        @DisplayName("deleteById: existing note returns true")
        void deleteByIdReturnsTrue() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(1);

            boolean result = repository.deleteById(1L);

            assertTrue(result);
            verify(ps, times(1)).executeUpdate();
        }

        @Test
        @DisplayName("deleteById: non-existing note returns false")
        void deleteByIdReturnsFalse() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(0);

            boolean result = repository.deleteById(99L);

            assertFalse(result);
        }

        @Test
        @DisplayName("existsById: existing id returns true")
        void existsByIdReturnsTrue() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            boolean result = repository.existsById(1L);

            assertTrue(result);
        }

        @Test
        @DisplayName("existsById: non-existing id returns false")
        void existsByIdReturnsFalse() throws SQLException {
            when(connection.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            boolean result = repository.existsById(99L);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Unhappy Path Tests")
    class UnhappyPath {

        @Test
        @DisplayName("save: null taskId in model constructor throws IllegalArgumentException")
        void saveWithNullTaskIdThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Note(Title.of("Title"), Description.of("body"), LocalDateTime.now(), LocalDateTime.now(), null)
            );
        }

        @Test
        @DisplayName("save (insert): SQLException is wrapped in DataAccessException")
        void saveInsertSQLExceptionWrapped() throws SQLException {
            Note note = new Note(
                    Title.of("Meeting Notes"),
                    Description.of("body"),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    1L
            );
            when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.save(note));
        }

        @Test
        @DisplayName("save (update): SQLException is wrapped in DataAccessException")
        void saveUpdateSQLExceptionWrapped() throws SQLException {
            Note note = createSampleNote();
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.save(note));
        }

        @Test
        @DisplayName("findById: null id throws AgendaException")
        void findByIdWithNullIdThrows() {
            assertThrows(AgendaException.class, () -> repository.findById(null));
        }

        @Test
        @DisplayName("findById: SQLException is wrapped in DataAccessException")
        void findByIdSQLExceptionWrapped() throws SQLException {
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.findById(1L));
        }

        @Test
        @DisplayName("findAllByTaskId: null taskId throws AgendaException")
        void findAllByTaskIdWithNullIdThrows() {
            assertThrows(AgendaException.class, () -> repository.findAllByTaskId(null));
        }

        @Test
        @DisplayName("findAllByTaskId: SQLException is wrapped in DataAccessException")
        void findAllByTaskIdSQLExceptionWrapped() throws SQLException {
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.findAllByTaskId(1L));
        }

        @Test
        @DisplayName("findAll: SQLException is wrapped in DataAccessException")
        void findAllSQLExceptionWrapped() throws SQLException {
            when(connection.createStatement()).thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.findAll());
        }

        @Test
        @DisplayName("deleteById: null id throws AgendaException")
        void deleteByIdWithNullIdThrows() {
            assertThrows(AgendaException.class, () -> repository.deleteById(null));
        }

        @Test
        @DisplayName("deleteById: SQLException is wrapped in DataAccessException")
        void deleteByIdSQLExceptionWrapped() throws SQLException {
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.deleteById(1L));
        }

        @Test
        @DisplayName("existsById: null id throws AgendaException")
        void existsByIdWithNullIdThrows() {
            assertThrows(AgendaException.class, () -> repository.existsById(null));
        }

        @Test
        @DisplayName("existsById: SQLException is wrapped in DataAccessException")
        void existsByIdSQLExceptionWrapped() throws SQLException {
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(DataAccessException.class, () -> repository.existsById(1L));
        }
    }
}
