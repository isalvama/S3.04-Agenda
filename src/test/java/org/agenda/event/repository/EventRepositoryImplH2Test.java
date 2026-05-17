package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventSchedule;
import org.agenda.event.model.EventType;
import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class EventRepositoryImplH2Test {

    private Connection h2Connection;
    private EventRepositoryImpl eventRepositoryImpl;


    @BeforeEach
    void setUp() throws SQLException {
        h2Connection = DriverManager.getConnection("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");

        try (Statement st = h2Connection.createStatement()) {
            st.execute("CREATE TABLE EVENT (" +
                    "id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(100) NOT NULL , " +
                    "body TEXT, " +
                    "date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "type ENUM('BIRTHDATE','APPOINTMENT','REMINDER','OTHER') NOT NULL DEFAULT 'OTHER', " +
                    "schedule ENUM('YEARLY','MONTHLY','WEEKLY','DAILY', 'HOURLY'), " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
        }
        eventRepositoryImpl = new EventRepositoryImpl(h2Connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        h2Connection.close();
    }

    @Test
    @DisplayName("create() method should save event in H2 and generate an ID")
    void createShouldHaveEventInH2AndGenerateAnId() {
        String title = "H2 Meeting";
        String desc = "Testing with real DB";
        String type = "OTHER";
        CalendarEvent event = new CalendarEvent(
                null,
                Title.of(title),
                Description.of(desc),
                LocalDateTime.now(),
                EventType.valueOf(type),
                null
        );

        Optional<CalendarEvent> result = eventRepositoryImpl.save(event);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNotNull(result.get().getId());
        Assertions.assertEquals("H2 Meeting", result.get().getTitle().value());

        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(1, rs.getInt(1));
                    }
                }
        );
    }

    @Test
    @DisplayName("create() method should save event with all columns filled and generate an ID")
    void createShouldSaveAnEventInH2WithAllColumnsFilledAndGenerateAnId() {
        String title = "Test 2";
        String desc = "Testing with real DB";
        LocalDateTime date = LocalDateTime.of(2027, 12, 23, 12, 0);
        String type = "OTHER";
        String schedule = "YEARLY";
        CalendarEvent event = new CalendarEvent(
                null,
                Title.of(title),
                Description.of(desc),
                date,
                EventType.valueOf(type),
                EventSchedule.valueOf(schedule)
        );

        Optional<CalendarEvent> result = eventRepositoryImpl.save(event);

        Assertions.assertTrue(result.isPresent());

        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(1, rs.getInt(1));
                    }
                }
        );

        Optional<CalendarEvent> result2 = eventRepositoryImpl.save(event);
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals(2L, result2.get().getId());
        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(2, rs.getInt(1));
                    }
                }
        );
    }

    @Test
    @DisplayName("create() method should save event with only a title, date and type")
    void createShouldSaveEventInH2WithOnlyTitleDateAndTypeAndGenerateAnId() {
        String title = "Event 3";
        LocalDateTime date = LocalDateTime.of(2025, 1, 15, 6, 0);
        String type = "OTHER";
        CalendarEvent event = new CalendarEvent(
                null,
                Title.of(title),
                null,
                date,
                EventType.valueOf(type),
                null
        );

        Optional<CalendarEvent> result = eventRepositoryImpl.save(event);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNotNull(result.get().getId());
        Assertions.assertEquals(title, result.get().getTitle().value());
        Assertions.assertNull(result.get().getDescription().map(Description::value).orElse(null));
        Assertions.assertEquals(date, result.get().getDate());
        Assertions.assertEquals(type, result.get().getType().name());
        Assertions.assertNull(result.get().getSchedule().map(EventSchedule::name).orElse(null));


        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(1, rs.getInt(1));
                    }
                }
        );
    }

    @Test
    @DisplayName("update() method should update event with the stated id and parameters passed")
    void updateShouldUpdateEventInH2WithOnlyTitleDateAndTypeAndGenerateAnId() {
        CalendarEvent event = new CalendarEvent(
                null,
                Title.of("Event 1"),
                Description.of("Description of Event 1"),
                LocalDateTime.of(2027, 1, 15, 6, 0),
                EventType.valueOf("OTHER"),
                null
        );
        eventRepositoryImpl.save(event);

        CalendarEvent event2 = new CalendarEvent(
                null,
                Title.of("Event 2"),
                Description.of("Description of Event 2"),
                LocalDateTime.of(2027, 4, 20, 11, 0),
                EventType.valueOf("BIRTHDATE"),
                EventSchedule.valueOf("YEARLY")
        );
        Optional<CalendarEvent> eventBeforeUpdate = eventRepositoryImpl.save(event2);

        Assertions.assertEquals("Event 2", eventBeforeUpdate.get().getTitle().value());
        Assertions.assertEquals("Description of Event 2", eventBeforeUpdate.get().getDescription().map(Description::value).orElse(null));
        Assertions.assertEquals(LocalDateTime.of(2027, 4, 20, 11, 0), eventBeforeUpdate.get().getDate());
        Assertions.assertEquals("BIRTHDATE", eventBeforeUpdate.get().getType().name());
        Assertions.assertEquals("YEARLY", eventBeforeUpdate.get().getSchedule().map(EventSchedule::name).orElse(null));

        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(2, rs.getInt(1));
                    }
                }
        );

        CalendarEvent event2Update = new CalendarEvent(
                2L,
                Title.of("Event 2 updated"),
                Description.of("Description of Event 2 updated"),
                LocalDateTime.of(2027, 4, 20, 11, 0),
                EventType.valueOf("BIRTHDATE"),
                null
        );

        Optional<CalendarEvent> updatedEvent = eventRepositoryImpl.save(event2Update);

        Assertions.assertEquals("Event 2 Updated", updatedEvent.get().getTitle().value());
        Assertions.assertEquals("Description of Event 2 updated", updatedEvent.get().getDescription().map(Description::value).orElse(null));
        Assertions.assertEquals(LocalDateTime.of(2027, 4, 20, 11, 0), updatedEvent.get().getDate());
        Assertions.assertEquals("BIRTHDATE", updatedEvent.get().getType().name());
        Assertions.assertNull(updatedEvent.get().getSchedule().map(EventSchedule::name).orElse(null));
    }

    @Test
    @DisplayName("update() method should throw DataAccessException when passing event with unexistent id")
    void updateShouldThrowDataAccessExceptionWhenPassingEventWithUnexistentId() {
        CalendarEvent unexistentIdEvent = new CalendarEvent(
                3L,
                Title.of("Event 3"),
                Description.of("Description of Event 3"),
                LocalDateTime.of(2027, 4, 20, 11, 0),
                EventType.valueOf("APPOINTMENT"),
                null
        );

        Assertions.assertThrows(EventNotUpdatedException.class, () -> {
            eventRepositoryImpl.updateById(unexistentIdEvent);
        });
    }


    @Test
    @DisplayName("delete() method should delete event with the passed id")
    void shouldDeleteEventInH2WithOnlyWithPassedId() {
        CalendarEvent event = new CalendarEvent(
                null,
                Title.of("Event 1"),
                Description.of("Description of Event 1"),
                LocalDateTime.of(2027, 1, 15, 6, 0),
                EventType.valueOf("OTHER"),
                null
        );
        eventRepositoryImpl.save(event);

        CalendarEvent event2 = new CalendarEvent(
                null,
                Title.of("Event 2"),
                Description.of("Description of Event 2"),
                LocalDateTime.of(2027, 4, 20, 11, 0),
                EventType.valueOf("BIRTHDATE"),
                EventSchedule.valueOf("YEARLY")
        );
        Optional<CalendarEvent> eventBeforeUpdate = eventRepositoryImpl.save(event2);

        Assertions.assertEquals("Event 2", eventBeforeUpdate.get().getTitle().value());
        Assertions.assertEquals("Description of Event 2", eventBeforeUpdate.get().getDescription().map(Description::value).orElse(null));
        Assertions.assertEquals(LocalDateTime.of(2027, 4, 20, 11, 0), eventBeforeUpdate.get().getDate());
        Assertions.assertEquals("BIRTHDATE", eventBeforeUpdate.get().getType().name());
        Assertions.assertEquals("YEARLY", eventBeforeUpdate.get().getSchedule().map(EventSchedule::name).orElse(null));

        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(2, rs.getInt(1));
                    }
                }
        );

        boolean isEvent1Deleted = eventRepositoryImpl.deleteById(1);
        Assertions.assertTrue(isEvent1Deleted);
        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(1, rs.getInt(1));
                    }
                }
        );
        boolean isEvent2Deleted = eventRepositoryImpl.deleteById(2);
        Assertions.assertTrue(isEvent2Deleted);
        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(0, rs.getInt(1));
                    }
                }
        );
    }


    @Test
    void deleteShouldReturnFalseWhenIdDoesNotExist() {
        boolean result = eventRepositoryImpl.deleteById(999L);
        Assertions.assertFalse(result);
    }


    @Test
    @DisplayName("findById() should return the events requested by the id")
    void shouldReturnListOfEventWithTheSameId() {
        String title1 = "Title 1";
        String desc1 = "Desc of Event 1";
        java.time.LocalDateTime date1 = LocalDateTime.of(2028, 12, 25, 11, 0);
        String type1 = "REMINDER";
        String schedule1 = "MONTHLY";
        CalendarEvent event1 = new CalendarEvent(
                null,
                Title.of(title1),
                Description.of(desc1),
                date1,
                EventType.valueOf(type1),
                EventSchedule.valueOf(schedule1)
        );
        eventRepositoryImpl.save(event1);

        String title2 = "Title 2";
        String desc2 = "Desc of Event 2";
        java.time.LocalDateTime date2 = LocalDateTime.of(2027, 6, 22, 13, 0);
        String type2 = "BIRTHDATE";


        CalendarEvent event2 = new CalendarEvent(
                null,
                Title.of(title2),
                Description.of(desc2),
                date2,
                EventType.valueOf(type2),
                null
        );

        eventRepositoryImpl.save(event2);

        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(2, rs.getInt(1));
                    }
                }
        );
        Long id = 2L;
        Optional<CalendarEvent> foundEvent = eventRepositoryImpl.findById(id);
        Assertions.assertTrue(foundEvent.isPresent());
        Assertions.assertEquals(id, foundEvent.get().getId());
        Assertions.assertEquals(title2, foundEvent.get().getTitle().value());
        Assertions.assertEquals(desc2, foundEvent.get().getDescription().map(Description::value).orElse(null));
        Assertions.assertEquals(date2, foundEvent.get().getDate());
        Assertions.assertEquals(type2, foundEvent.get().getType().name());
        Assertions.assertNull(foundEvent.get().getSchedule().orElse(null));

        Long id2 = 1L;
        Optional<CalendarEvent> foundEvent2 = eventRepositoryImpl.findById(id2);
        Assertions.assertTrue(foundEvent2.isPresent());
        Assertions.assertEquals(id2, foundEvent2.get().getId());
        Assertions.assertEquals(title1, foundEvent2.get().getTitle().value());
        Assertions.assertEquals(desc1, foundEvent2.get().getDescription().map(Description::value).orElse(null));
        Assertions.assertEquals(date1, foundEvent2.get().getDate());
        Assertions.assertEquals(type1, foundEvent2.get().getType().name());
        Assertions.assertEquals(schedule1, foundEvent2.get().getSchedule().map(EventSchedule::name).orElse(null));
    }


    @Test
    @DisplayName("findAll() should return a list of all the existent events in the database")
    void shouldReturnListOfAllEvents() {
        CalendarEvent event1 = new CalendarEvent(
                null,
                Title.of("Event 1"),
                Description.of("Description of Event 1"),
                LocalDateTime.of(2027, 1, 15, 6, 0),
                EventType.valueOf("OTHER"),
                null
        );
        eventRepositoryImpl.save(event1);

        List<CalendarEvent> allEventsBefore = eventRepositoryImpl.findAll();

        Assertions.assertEquals(1, allEventsBefore.size());
        Assertions.assertEquals("Event 1", allEventsBefore.getFirst().getTitle().value());
        Assertions.assertEquals(LocalDateTime.of(2027, 1, 15, 6, 0), allEventsBefore.getFirst().getDate());


        CalendarEvent event2 = new CalendarEvent(
                null,
                Title.of("Event 2"),
                Description.of("Description of Event 2"),
                LocalDateTime.of(2027, 4, 20, 11, 0),
                EventType.valueOf("BIRTHDATE"),
                EventSchedule.valueOf("YEARLY")
        );

        eventRepositoryImpl.save(event2);

        List<CalendarEvent> allEventsAfter = eventRepositoryImpl.findAll();

        Assertions.assertEquals(2, allEventsAfter.size());
        Assertions.assertEquals("Event 1", allEventsAfter.getFirst().getTitle().value());
        Assertions.assertEquals("Event 2", allEventsAfter.get(1).getTitle().value());
        Assertions.assertEquals(LocalDateTime.of(2027, 1, 15, 6, 0), allEventsAfter.getFirst().getDate());
        Assertions.assertEquals(LocalDateTime.of(2027, 4, 20, 11, 0), allEventsAfter.get(1).getDate());
    }

    @Test
    @DisplayName("findUpcomingEvents() should return a list of the events with a date within the interval of days passed")
    void shouldReturnListOfEventsWithDateWithinTheIntervalDays() {
        CalendarEvent event1 = new CalendarEvent(
                null,
                Title.of("Event 1"),
                Description.of("Description of Event 1"),
                LocalDateTime.now().plusDays(3),
                EventType.valueOf("OTHER"),
                null
        );
        eventRepositoryImpl.save(event1);

        CalendarEvent event2 = new CalendarEvent(
                null,
                Title.of("Event 2"),
                Description.of("Description of Event 2"),
                LocalDateTime.now().plusDays(5),
                EventType.valueOf("BIRTHDATE"),
                EventSchedule.valueOf("YEARLY")
        );
        eventRepositoryImpl.save(event2);

        Assertions.assertDoesNotThrow(() -> {
                    try (Statement st = h2Connection.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM EVENT")) {
                        rs.next();
                        Assertions.assertEquals(2, rs.getInt(1));
                    }
                }
        );

        List<CalendarEvent> allEvents = eventRepositoryImpl.findUpcomingEvents(10);

        Assertions.assertEquals(2, allEvents.size());
        Assertions.assertEquals("Event 1", allEvents.getFirst().getTitle().value());
        Assertions.assertEquals("Event 2", allEvents.get(1).getTitle().value());
    }

    @Test
    @DisplayName("findUpcomingEvents() should throw EventDataAccessConnection when passing a negative number as interval days")
    void shouldThrowEventDataAccessConnection() {
        Assertions.assertThrows(EventDataAccessConnection.class, () -> eventRepositoryImpl.findUpcomingEvents(-10));
    }
}