package org.agenda.task.repository;

import org.agenda.task.model.Task;
import org.agenda.task.model.Status;
import org.agenda.task.model.Priority;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryImpl implements TaskRepository {

    private final Connection connection;

    public TaskRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Task save(Task task) {
        return (task.getId() == null) ? insert(task) : update(task);
    }

    public Task insert(Task task) {
        String sql = "INSERT INTO task (title, body, status, priority, expiration_date, event_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            mapTaskToStatement(ps, task);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) task.setId(rs.getLong(1));
            }
            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting task: " + task.getTitle(), e);
        }
    }

    private Task update(Task task) {
        String sql = "UPDATE task SET title=?, body=?, status=?, priority=?, expiration_date=?, event_id=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            mapTaskToStatement(ps, task);
            ps.setLong(7, task.getId());
            ps.executeUpdate();
            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating task ID: " + task.getId(), e);
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM task WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRowToTask(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching task " + id, e);
        }
    }

    @Override
    public List<Task> findAllByStatus(Status status) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.getSqlValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tasks.add(mapRowToTask(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching tasks by status: " + status.getSqlValue(), e);
        }
        return tasks;
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) tasks.add(mapRowToTask(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error listing all tasks", e);
        }
        return tasks;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM task WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting task " + id, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT * FROM task WHERE id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void mapTaskToStatement(PreparedStatement ps, Task task) throws SQLException {
        ps.setString(1, task.getTitle());
        ps.setString(2, task.getBody());
        ps.setString(3, task.getStatus().getSqlValue());
        ps.setString(4, task.getPriority().map(Priority::getSqlValue).orElse(null));
        ps.setTimestamp(5, Timestamp.valueOf(task.getExpirationDate()));

        if (task.getEventId().isPresent()) {
            ps.setLong(6, task.getEventId().get());
        } else {
            ps.setNull(6, Types.BIGINT);
        }
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException {
        return new Task(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("body"),
                Status.fromSqlValue(rs.getString("status")),
                Priority.fromSqlValue(rs.getString("priority")).orElse(null),
                rs.getTimestamp("expiration_date").toLocalDateTime(),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getObject("event_id") != null ? rs.getLong("event_id") : null);
    }
}