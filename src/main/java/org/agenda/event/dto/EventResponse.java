package org.agenda.event.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventResponse (long id, String title, LocalDateTime date, List<String> warnings){
}
