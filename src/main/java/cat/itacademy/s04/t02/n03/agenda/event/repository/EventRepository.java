package cat.itacademy.s04.t02.n03.agenda.event.repository;

import cat.itacademy.s04.t02.n03.agenda.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    void save(Event event);
    Optional<Event> findById(int id);
    List<Event> findAll();
}