package org.agenda.event.dto;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record EventResponse (Long id, String title, @Nullable String description, LocalDateTime date, String type, @Nullable String schedule, List<String> warnings){
}
