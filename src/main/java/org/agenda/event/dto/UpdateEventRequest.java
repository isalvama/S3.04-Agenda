package org.agenda.event.dto;

import org.agenda.shared.domain.value_object.Description;
import org.agenda.shared.domain.value_object.Title;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public record UpdateEventRequest(int id, @Nullable String title, @Nullable String description, @Nullable LocalDateTime date, @Nullable String type, @Nullable String eventSchedule) {

}
