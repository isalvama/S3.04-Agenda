package org.agenda.shared.console;

import org.agenda.event.model.EventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class ConsoleReaderTest {
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp(){
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void readLongShouldShowErrorInvalidMessageAndRetry(){
        String input = "not_a_number\n100\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        Long result = ConsoleReader.readLong("Write a number:");
        Assertions.assertEquals(100L, result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput
                .contains("Invalid Input: "));
        Assertions.assertTrue(consoleOutput.contains("Please enter a valid number"));
    }

    @Test
    void readLongShouldShowErrorNegativeNumberMessageAndRetry(){
        String input = "-5\n50\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        Long result = ConsoleReader.readLong("Write a number:");
        Assertions.assertEquals(50L, result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput
                .contains("Invalid Input: Number cannot be negative. Please enter a valid number"));
    }

    @Test
    void readIntShouldShowErrorInvalidMessageAndRetry(){
        String input = "6e\n365\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        int result = ConsoleReader.readInt("Write a number:");
        Assertions.assertEquals(365, result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput
                .contains("Invalid Input: "));
        Assertions.assertTrue(consoleOutput.contains("Please enter a valid number"));
    }

    @Test
    void readStringShouldShowErrorMinLengthMessageAndRetry(){
        String input = "e\nHello\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        String result = ConsoleReader.readString("Write a word with more than 1 word:", 2);
        Assertions.assertEquals("Hello", result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput.contains("Invalid Input: The input can't consist of less 2 letter/s"));

    }

    @Test
    void readStringShouldShowErrorBlankSpaceMessageAndRetry(){
        String input = " \nHello\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        String result = ConsoleReader.readString("Write a word with more than 1 word:", 2);
        Assertions.assertEquals("Hello", result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput.contains("Invalid Input: The input can't be a blank space"));
    }

    @Test
    void readDateShouldShowInvalidDateFormatMessageAndRetry(){
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String input = "hello\n13/11/2026 13:00\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        LocalDateTime date = LocalDateTime.of(2026, 11, 13, 13, 0);

        LocalDateTime result = ConsoleReader.readDate("Write a date: ", DATE_FORMAT, LocalDateTime.now().plusDays(5));
        Assertions.assertEquals(date, result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput.contains("Invalid Date Format:"));
        Assertions.assertTrue(consoleOutput.contains("Please use the indicated date format."));

    }

    @Test
    void readDateUnexistentDateShouldShowErrorMessageAndRetry(){
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String input = "34/13/2027 10:00\n06/04/1995 09:00\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        LocalDateTime date = LocalDateTime.of(1995, 4, 6, 9, 0);

        LocalDateTime result = ConsoleReader.readDate("Write a date: ", DATE_FORMAT, LocalDateTime.now().plusDays(5));
        Assertions.assertEquals(date, result);

        String consoleOutput = outputStream.toString();
        Assertions.assertTrue(consoleOutput.contains("Invalid Date Format:"));
        Assertions.assertTrue(consoleOutput.contains("Please use the indicated date format."));
    }

    @Test
    void readEnumWithNameInLowerCaseShouldReturnStringInUpperCase() {
        String input = "birthdate\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        String result = ConsoleReader.readEnumName(EventType.class, "Select type: ");
        String consoleOutput = outputStream.toString();

        Assertions.assertEquals("BIRTHDATE", result);
    }

    @Test
    void readEnumShouldReturnNull() {
        String input = "\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        String result = ConsoleReader.readEnumName(EventType.class, "Select type: ");
        String consoleOutput = outputStream.toString();

        Assertions.assertNull(result);
    }

    @Test
    void readEnumShouldShowErrorMessageAndRetry() {

        String input = "ERROR\nAPPOINTMENT\n";
        ConsoleReader.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));
        System.setOut(new PrintStream(outputStream));

        String result = ConsoleReader.readEnumName(EventType.class, "Select type: ");
        String consoleOutput = outputStream.toString();

        Assertions.assertEquals("APPOINTMENT", result);

        Assertions.assertTrue(consoleOutput.contains("does not match with"));
    }

}
