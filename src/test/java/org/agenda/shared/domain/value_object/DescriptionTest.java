package org.agenda.shared.domain.value_object;

import org.agenda.shared.domain.exception.InvalidDescriptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionTest {

    @Test
    void shouldCreateValidFormattedDescription() {
        Description desc = Description.of("quick review of goals and pending tasks for the week.");
        assertEquals("Quick review of goals and pending tasks for the week.", desc.value());
    }

    @Test
    void shouldTrimDescription() {
        Description desc = Description.of("   status check and defining next steps.   ");
        assertEquals("Status check and defining next steps.", desc.value());
    }

    @Test
    void shouldThrowExceptionWhenNullOrBlank() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> Description.of(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> Description.of("")),
                () -> assertThrows(IllegalArgumentException.class, () -> Description.of("   "))
        );
    }

    @Test
    void shouldThrowExceptionWhenTooShort() {
        assertThrows(InvalidDescriptionException.class, () -> Description.of(" A "));
    }

    @Test
    void shouldThrowExceptionWhenTooLong() {
        String longDesc = "a".repeat(601);
        assertThrows(InvalidDescriptionException.class, () -> Description.of(longDesc));
    }

    @ParameterizedTest
    @CsvSource({"ab,Ab", "this is a description with a valid longitude,This is a description with a valid longitude"})
    void shouldAllowBoundaryLengths(String boundaryText, String formattedDesc) {
        Description desc = Description.of(boundaryText);
        assertEquals(formattedDesc, desc.value());
    }

    @Test
    @DisplayName("toString() should return the desc value")
    void toStringShouldReturnValue() {
        String text = "My Description";
        Description desc = Description.of(text);
        assertEquals(text, desc.value());
    }
}