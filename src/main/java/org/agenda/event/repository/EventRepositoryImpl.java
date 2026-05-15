package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.shared.config.DatabaseConnection;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepositoryImpl implements EventRepository {

    @Override
    public Optional<CalendarEvent> save(CalendarEvent event) {
        String sql = "INSERT INTO EVENT(TITLE, BODY, DATE, TYPE, SCHEDULE, CREATED_AT) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
           mapEventToStatement(ps, event);
           ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    event.setId(newId);
                    return Optional.of(event);
                }
            }
                return Optional.empty();
        } catch (SQLException ex) {
            throw new EventDataAccessConnection("Error saving the event in MySQL: " + ex.getMessage());
        }
    }

    @Override
    public void updateById(CalendarEvent event) {
        String sql = "UPDATE EVENT SET TITLE = ?, BODY = ?, DATE = ?, TYPE = ?, SCHEDULE = ?, UPDATED_AT = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            mapEventToStatement(ps, event);
            if (ps.executeUpdate() > 0) throw new EventDataAccessConnection((String.format("Error saving the event %s with id %s in MySQL. Event info: %s", event.getTitle(), event.getId(), event.toString())));
        } catch (SQLException ex) {
            throw new EventDataAccessConnection("Error saving the event in MySQL: " + ex.getMessage());
        }
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM EVENT WHERE id = ?;";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new EventDataAccessConnection("Error updating the event with id " + id + " in MySQL: " + ex.getMessage());
        }
    }

    @Override
    public Optional<CalendarEvent> findById(long id) {
        String sql = "SELECT id, title, body, date, type FROM EVENT WHERE ID = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapStatementToEvent(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new EventDataAccessConnection("Error finding the Event with id " + id + " in MySQL: " + ex.getMessage());
        }
    }


    @Override
    public List<CalendarEvent> findAll() {
        String sql = "SELECT id, title, body, date, type, schedule FROM event";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {

                List<CalendarEvent> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(mapStatementToEvent(rs));
                }
                return events;
            }
        } catch (SQLException ex) {
            throw new EventDataAccessConnection("Error finding events in Event table: " + ex.getMessage());
        }
    }

    @Override
    public List<CalendarEvent> findUpcomingEvents(int intervalDays) {
        String sql = "SELECT id, title, body, date, type, schedule FROM EVENT WHERE date >= NOW() - INTERVAL ? DAY;";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, intervalDays);
            try (ResultSet rs = ps.executeQuery()) {

                List<CalendarEvent> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(mapStatementToEvent(rs));
                }
                return events;
            }
        } catch (SQLException e) {
            throw new EventDataAccessConnection("Error finding events in Event table: " + e.getMessage());
        }
    }

    private void mapEventToStatement(PreparedStatement ps, CalendarEvent event) throws SQLException {
        ps.setString(1, event.getTitle().value());
        ps.setString(2, event.getDescription().map(Description::value).orElse(null));
        ps.setTimestamp(3, Timestamp.valueOf(event.getDate()));
        ps.setObject(4, event.getType().name());
        ps.setString(5, event.getSchedule().map(Enum::name).orElse(null));
        ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        if (event.getId() == null) {
            ps.setLong(7, event.getId());
        }
    }

    private CalendarEvent mapStatementToEvent(ResultSet rs) throws SQLException {

        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String bodyRaw = rs.getString("body");
        LocalDateTime date = rs.getObject("date", LocalDateTime.class);
        String type = rs.getString("type");
        String eventScheduleRaw = rs.getString("schedule");

        Description body = bodyRaw != null ? Description.of(bodyRaw) : null;
        EventSchedule eventSchedule = eventScheduleRaw != null ? EventSchedule.valueOf(eventScheduleRaw) : null;

        return new CalendarEvent(id, Title.of(title), body, date, EventType.valueOf(type), eventSchedule);
    }
}

