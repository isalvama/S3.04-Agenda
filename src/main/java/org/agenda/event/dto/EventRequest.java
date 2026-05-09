package org.agenda.event.dto;

import java.time.LocalDateTime;

public record EventRequest(String title, String description, LocalDateTime date, String type, String eventSchedule){}