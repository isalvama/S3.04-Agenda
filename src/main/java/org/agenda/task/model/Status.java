package org.agenda.task.model;

import java.util.Arrays;
import java.util.Optional;

public enum Status {
    PENDING("PENDING"),
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE");

    private final String sqlValue;

    Status(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public static Status fromSqlValue(String value) {
        if (value == null || value.isBlank()) {
            return Status.PENDING;
        }

        return Arrays.stream(Status.values())
                .filter(s -> s.getSqlValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(Status.PENDING);

    }
}