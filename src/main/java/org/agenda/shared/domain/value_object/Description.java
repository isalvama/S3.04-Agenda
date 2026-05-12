package org.agenda.shared.domain.value_object;

import org.agenda.shared.domain.exception.InvalidDescriptionException;
import org.jetbrains.annotations.NotNull;

public record Description(String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 600;

    public Description {
        if (value == null || value.isBlank()) throw new InvalidDescriptionException("Description can not be null or blank");
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidDescriptionException(String.format("Description exceeds the limit of %d characters", MAX_LENGTH)
            );
        }
        value = format(value);
    }

    private String format(String value){
        return String.format("%s%s", value.substring(0,1).toUpperCase(), value.substring(1));
    }

    public static Description of(String value) {
        return new Description(value);
    }

    @NotNull
    @Override
    public String value() {
        return value;
    }
}

