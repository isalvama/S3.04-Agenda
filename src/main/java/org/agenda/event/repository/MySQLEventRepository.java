package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;
import org.agenda.shared.config.DatabaseConnection;
import org.agenda.shared.domain.exception.DataAccessException;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class MySQLEventRepository implements EventRepository{
    @Override
    public CalendarEvent save(CalendarEvent event) {
            String sql = "INSERT INTO EVENT(TITLE, BODY, DATE, TYPE, SCHEDULE) VALUES (?, ?, ?, ?, ?)";

            try (Connection connection = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, event.getTitle().value());
                ps.setString(2, event.getDescription().toString());
                ps.setTimestamp(3, Timestamp.valueOf(event.getDate()));
                ps.setObject(4, event.getType().name());
                ps.setString(5, (event.getSchedule() != null) ? event.getSchedule().name() : null);
                ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int nextId = generatedKeys.getInt(1);
                    event.setId(nextId);
                }
                return event;
            }
        } catch (SQLException ex) {
                throw new DataAccessException("Error saving the event in MySQL:");
            }
    }

    @Override
    public Optional<CalendarEvent> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<CalendarEvent> findAll() {
        return List.of();
    }
}
