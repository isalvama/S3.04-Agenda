package org.agenda.shared.domain.value_object;

import org.agenda.shared.domain.exception.InvalidTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class TitleTest {

    @Test
    void shouldCreateValidFormattedTitle() {
        Title title = Title.of("Work meeting");
        assertEquals("Work Meeting", title.value());
    }

    @Test
    void shouldTrimTitle() {
        Title title = Title.of("   title with spaces   ");
        assertEquals("Title With Spaces", title.value());
    }

    @Test
    void shouldThrowExceptionWhenNullOrBlank() {
        assertAll(
                () -> assertThrows(InvalidTitleException.class, () -> Title.of(null)),
                () -> assertThrows(InvalidTitleException.class, () -> Title.of("")),
                () -> assertThrows(InvalidTitleException.class, () -> Title.of("   "))
        );
    }

    @Test
    void shouldThrowExceptionWhenTooShort() {
        assertThrows(InvalidTitleException.class, () -> Title.of(" A "));
    }

    @Test
    void shouldThrowExceptionWhenTooLong() {
        String longTitle = "a".repeat(101);
        assertThrows(InvalidTitleException.class, () -> Title.of(longTitle));
    }

    @ParameterizedTest
    @CsvSource({"AB,AB", "This is a title with a valid longitude,This Is A Title With A Valid Longitude"})
    void shouldAllowBoundaryLengths(String boundaryText, String formattedTitle) {
        Title title = Title.of(boundaryText);
        assertEquals(formattedTitle, title.value());
    }

    @Test
    @DisplayName("toString() should return the title value")
    void toStringShouldReturnValue() {
        String text = "My Title";
        Title title = Title.of(text);
        assertEquals(text, title.toString());
    }
}