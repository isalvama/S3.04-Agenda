package org.agenda.note.repository;

import org.agenda.note.dto.NoteRequest;
import org.agenda.note.model.Note;
import org.agenda.shared.exception.AgendaException;

import javax.management.openmbean.InvalidOpenTypeException;
import java.sql.*;
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
        validateRequest(new NoteRequest(note.getTitle(), note.getBody(), note.getTaskId()));
        return (note.getId() == null) ? insert(note) : update(note);
    }

    public Note insert(Note note){
        String sql = "INSERT INTO note(title, body, task_id) VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            mapNoteToStatement(ps, note);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) note.setId(rs.getLong(   1));
            }
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting task: " + note.getTitle(), e);
        }
    }

    public Note update(Note note){
        validateId(note.getId());
        validateRequest(new NoteRequest(note.getTitle(), note.getBody(), note.getTaskId()));
        String sql = "UPDATE note SET title = ?, body = ?, task_id = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            mapNoteToStatement(ps, note);
            ps.setLong(4, note.getId());
            ps.executeUpdate();
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating note ID: " + note.getId(), e);
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
            throw new RuntimeException("Error fetching note " + id, e);
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
            throw new RuntimeException("Error fetching notes by task: " + taskId, e);
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
            throw new RuntimeException("Error listing all notes", e);
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
            throw new RuntimeException("Error deleting note " + id, e);
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
            throw new RuntimeException("Error checking existence of note " + id, e);
        }
    }

    private void mapNoteToStatement(PreparedStatement ps,  Note note) throws SQLException{
        ps.setString(1, note.getTitle());
        ps.setString(2, note.getBody());
        ps.setLong(3, note.getTaskId());
    }

    private Note mapRowToNote(ResultSet rs) throws SQLException{
        return new Note(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("body"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
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
