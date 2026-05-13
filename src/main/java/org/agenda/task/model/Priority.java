package org.agenda.task.model;

import java.util.Arrays;
import java.util.Optional;

public enum Priority {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String sqlValue;

    Priority(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public static Optional<Priority> fromSqlValue(String value) {
        if (value == null) return Optional.empty();

        return Arrays.stream(Priority.values())
                .filter(p -> p.getSqlValue().equalsIgnoreCase(value))
                .findFirst();
    }
}