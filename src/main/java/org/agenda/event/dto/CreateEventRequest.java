package org.agenda.event.dto;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public record CreateEventRequest(String title, @Nullable String description, LocalDateTime date, @Nullable String type, @Nullable String eventSchedule){}