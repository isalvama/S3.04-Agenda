package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;
import org.agenda.shared.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MySQLEventRepository implements EventRepository{
    @Override
    public void save(CalendarEvent event) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO EVENT(TITLE, BODY, DATE, TYPE, SCHEDULE, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setObject(1, event.getTitle().value());
            ps.setObject(2, event.getDescription());
            ps.setObject(3, event.getDate());
            ps.setObject(4, event.getType());
            ps.setObject(5, event.getSchedule());
            ps.setObject(6, LocalDateTime.now());
            ps.setObject(7, LocalDateTime.now());
        } catch (SQLException e) {
            throw new RuntimeException(e); // TODO REVIEW
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
