package org.agenda.event.repository;

import org.agenda.event.model.CalendarEvent;
import org.agenda.event.model.EventType;
import org.agenda.shared.domain.value_object.Title;

import java.time.LocalDateTime;

public class MySQLEventRepositoryTest {
    public static void main(String[] args) {
        try {
            System.out.println("--- Iniciando Prueba Manual ---");

            // 1. Crear datos de prueba
            CalendarEvent event = new CalendarEvent(0, Title.of("Work"), null, LocalDateTime.now(), EventType.APPOINTMENT, null);


            MySQLEventRepository repo = new MySQLEventRepository();

            System.out.println("Guardando reserva...");
            System.out.println(repo.save(event));

            // 3. Verificar resultados
            System.out.println(event.getId());

        } catch (Exception e) {
            System.err.println("💥 FALLÓ LA PRUEBA:");
            e.printStackTrace();
        }
    }
}