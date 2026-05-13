package org.agenda.shared.domain.value_object;

import org.agenda.shared.domain.exception.InvalidTitleException;

import java.util.Arrays;
import java.util.stream.Collectors;

public record Title (String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public Title {
        if (value == null || value.isBlank()) throw new InvalidTitleException("Title can not be null or blank");

        value = value.trim();

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidTitleException(String.format("Title should have been %d and %d characters", MIN_LENGTH, MAX_LENGTH));
        }
        value = format(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private String format(String value){
       value = value.trim();
       if (value.length() == 1) return value.toUpperCase();
       return Arrays.stream(value.split("\\s+")).filter(w -> !w.isEmpty()).map(w ->
               w.substring(0, 1).toUpperCase() + w.substring(1)
       ).collect(Collectors.joining(" "));
    }

    public static Title of(String value) {
        return new Title(value);
    }
}