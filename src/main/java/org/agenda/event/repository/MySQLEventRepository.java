package org.agenda.event.repository;

import jdk.jfr.Event;
import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.shared.config.DatabaseConnection;
import org.agenda.shared.domain.exception.DataAccessException;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLEventRepository implements EventRepository {
    @Override
    public CalendarEvent save(CalendarEvent event) {
        String sql = "INSERT INTO EVENT(TITLE, BODY, DATE, TYPE, SCHEDULE) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getTitle().value());
            ps.setString(2, event.getDescription() != null ? event.getDescription().value() : null);
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
            throw new DataAccessException("Error saving the event in MySQL: " + ex.getMessage());
        }
    }

    @Override
    public CalendarEvent update(CalendarEvent event) {
        String sql = "UPDATE EVENT SET title = ?, body = ?, date = ?, type = ?, schedule = ?, updated_at = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getTitle().value());
            ps.setString(2, event.getDescription() != null ? event.getDescription().value() : null);
            ps.setTimestamp(3, Timestamp.valueOf(event.getDate()));
            ps.setObject(4, event.getType().name());
            ps.setString(5, (event.getSchedule() != null) ? event.getSchedule().name() : null);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(7, event.getId());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int nextId = generatedKeys.getInt(1);
                    event.setId(nextId);
                }
                return event;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error saving the event in MySQL: " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM EVENT WHERE id = ?;";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error updating the event with id " + id + " in MySQL: " + ex.getMessage());
        }
    }

    @Override
    public Optional<CalendarEvent> findById(int id) {
        String sql = "SELECT * FROM EVENT WHERE ID = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt("id");
                    String title = rs.getString("title");
                    String bodyRaw = rs.getString("body");
                    LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                    String type = rs.getString("type");
                    String eventScheduleRaw = rs.getString("schedule");

                    Description body = bodyRaw != null ? Description.of(bodyRaw) : null;
                    EventSchedule eventSchedule = eventScheduleRaw != null ? EventSchedule.valueOf(eventScheduleRaw) : null;
                    return Optional.of(new CalendarEvent(newId, Title.of(title), body, date, EventType.valueOf(type), eventSchedule));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error fing Event with id " + id + " in MySQL: " + ex.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public Optional<List<CalendarEvent>> findAll() {
        String sql = "SELECT * FROM EVENT";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            List<CalendarEvent> events = new ArrayList<>();

            while (rs.next()) {
                int newId = rs.getInt("id");
                String title = rs.getString("title");
                String bodyRaw = rs.getString("body");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                String type = rs.getString("type");
                String eventScheduleRaw = rs.getString("schedule");

                Description body = bodyRaw != null ? Description.of(bodyRaw) : null;
                EventSchedule eventSchedule = eventScheduleRaw != null ? EventSchedule.valueOf(eventScheduleRaw) : null;
                events.add(new CalendarEvent(newId, Title.of(title), body, date, EventType.valueOf(type), eventSchedule));
            }
            return Optional.of(events);
        } catch (SQLException e) {
            throw new DataAccessException("Error fing events in Event table");
        }
    }

    public List<CalendarEvent> findUpcomingEvents(int intervalDays) {
        String sql = "SELECT * FROM EVENT WHERE date >= NOW() - INTERVAL ? DAY;";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            List<CalendarEvent> events = new ArrayList<>();

            while (rs.next()) {
                int newId = rs.getInt("id");
                String title = rs.getString("title");
                String bodyRaw = rs.getString("body");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                String type = rs.getString("type");
                String eventScheduleRaw = rs.getString("schedule");

                Description body = bodyRaw != null ? Description.of(bodyRaw) : null;
                EventSchedule eventSchedule = eventScheduleRaw != null ? EventSchedule.valueOf(eventScheduleRaw) : null;
                events.add(new CalendarEvent(newId, Title.of(title), body, date, EventType.valueOf(type), eventSchedule));
            }
            return events;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

