package org.agenda.event.repository;

import org.agenda.event.service.EventService;
import org.agenda.event.service.EventServiceImpl;

public class MySQLEventRepositoryTest {
    public static void main(String[] args) {
        try {
//            System.out.println("--- Iniciando Prueba Manual ---");
//
//            // 1. Crear datos de prueba
//            CalendarEvent event = new CalendarEvent(0, Title.of("Work"),null, LocalDateTime.now(), EventType.APPOINTMENT, null);
//
            EventRepositoryImpl repo = new EventRepositoryImpl();
//
//            System.out.println("Guardando reserva...");
//            System.out.println(repo.save(event));

            // 3. Verificar resultados
            EventService eventService = new EventServiceImpl(repo);
//          System.out.println(event.getId());

            //UpdateEventRequest infoEventToUpdate = new UpdateEventRequest(4, Title.of("hiii"), Description.of("hello"), LocalDateTime.now(), "OTHER", "DAILY");
            System.out.println("Updeteando reserva...");
            System.out.println();
           // System.out.println(eventService.updateEvent(infoEventToUpdate));

//            DeleteEventRequest deleteEventRequest = new DeleteEventRequest(1);
//            eventService.deleteEvent(deleteEventRequest);

//            CreateEventRequest createEventRequest = new CreateEventRequest("school meeting", null, LocalDateTime.of(2026, 5, 16, 10, 30), "OTHER", null);
//            eventService.createEvent(createEventRequest);
            System.out.println(repo.findUpcomingEvents(7));



        } catch (Exception e) {
            System.err.println("💥 FALLÓ LA PRUEBA:");
            e.printStackTrace();
        }
    }
}