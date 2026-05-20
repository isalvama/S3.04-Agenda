package org.agenda.note.repository;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.model.Note;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.agenda.shared.exception.AgendaException;
import org.agenda.shared.exception.DataAccessException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteRepositoryImpl implements NoteRepository{
    private final Connection connection;

    public NoteRepositoryImpl(Connection connection){
        this.connection = connection;
    }

    @Override
    public Note save(Note note) {
        validateRequest(new NoteRequest(
                note.getTitle().value(),
                note.getBody() != null ? note.getBody().value() : null,
                note.getTaskId()
        ));
        return (note.getId() == null) ? insert(note) : update(note);
    }

    public Note insert(Note note){
        String sql = "INSERT INTO note(title, body, task_id, created_at, updated_at) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            ps.setString(1, note.getTitle().value());
            ps.setString(2, note.getBody() != null ? note.getBody().value() : null);
            ps.setLong(3, note.getTaskId());
            ps.setObject(4, now);
            ps.setObject(5, now);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) note.setId(rs.getLong(1));
            }
            return note;
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting note: " + note.getTitle() + ". " + e.getMessage());
        }
    }

    public Note update(Note note){
        validateId(note.getId());
        validateRequest(new NoteRequest(
                note.getTitle().value(),
                note.getBody() != null ? note.getBody().value() : null,
                note.getTaskId()));
        String sql = "UPDATE note SET title = ?, body = ?, task_id = ?, updated_at = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, note.getTitle().value());
            ps.setString(2, note.getBody() != null ? note.getBody().value() : null);
            ps.setLong(3, note.getTaskId());
            ps.setObject(4, LocalDateTime.now());
            ps.setLong(5, note.getId());
            ps.executeUpdate();
            return note;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating note ID: " + note.getId() + ". " + e.getMessage());
        }
    }

    @Override
    public Optional<Note> findById(Long id) {
        validateId(id);
        String sql = "SELECT * FROM note WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(mapRowToNote(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching note " + id + ". " + e.getMessage());
        }
    }

    @Override
    public List<Note> findAllByTaskId(Long taskId) {
        validateId(taskId);
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE task_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) notes.add(mapRowToNote(rs));
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error fetching notes by task: " + taskId + ". " + e.getMessage());
        }
        return notes;
    }

    @Override
    public List<Note> findAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note";
        try(Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            while (rs.next()) notes.add(mapRowToNote(rs));
        }catch (SQLException e){
            throw new DataAccessException("Error listing all notes. " + e.getMessage());
        }
        return notes;
    }

    @Override
    public boolean deleteById(Long id) {
        validateId(id);
        String sql = "DELETE FROM note WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }catch (SQLException e){
            throw new DataAccessException("Error deleting note: " + id + ". " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(Long id) {
        validateId(id);
        String sql = "SELECT * FROM note WHERE id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch (SQLException e){
            throw new DataAccessException("Error checking existence of note: " + id + ". " + e.getMessage());
        }
    }


    private Note mapRowToNote(ResultSet rs) throws SQLException{
        String bodyRaw = rs.getString("body");
        Description body = bodyRaw != null ? Description.of(bodyRaw) : null;

        return new Note(
                rs.getLong("id"),
                Title.of(rs.getString("title")),
                body,
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class),
                rs.getLong("task_id")
        );
    }

    private void validateRequest(NoteRequest request) {
        if (request == null || request.title() == null || request.title().isBlank()) {
            throw new AgendaException("Note title cannot be empty");
        }
        if(request.taskId() == null){
            throw new IllegalArgumentException("A note must have an associated task");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new AgendaException("Note id cannot be null");
        }
    }
}
