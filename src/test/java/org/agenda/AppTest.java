package org.agenda;

import org.agenda.app.App;
import org.agenda.note.controller.NoteController;
import org.agenda.task.controller.TaskController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppTest {

    @Mock
    private TaskController taskController;
    @Mock
    private NoteController noteController;

    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private void runMenuWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        App.showMainMenu(scanner, taskController, noteController);
    }

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("Option 1 delegates to TaskController")
        void option1DelegatesToTaskController() {
            runMenuWithInput("1\n0\n");
            verify(taskController, times(1)).showMenu();
        }

        @Test
        @DisplayName("Option 2 delegates to NoteController")
        void option2DelegatesToNoteController() {
            runMenuWithInput("2\n0\n");
            verify(noteController, times(1)).showMenu();
        }

        @Test
        @DisplayName("Option 3 shows event under development")
        void option3ShowsEventUnderDevelopment() {
            runMenuWithInput("3\n0\n");
            assertTrue(output.toString().contains("Event module under development"));
        }

        @Test
        @DisplayName("Option 0 exits with goodbye message")
        void option0ExitsWithGoodbye() {
            runMenuWithInput("0\n");
            assertTrue(output.toString().contains("Goodbye!"));
        }
    }

    @Nested
    @DisplayName("Unhappy Path")
    class UnhappyPath {

        @Test
        @DisplayName("Invalid option shows error message")
        void invalidOptionShowsError() {
            runMenuWithInput("99\n0\n");
            assertTrue(output.toString().contains("Invalid option"));
        }

        @Test
        @DisplayName("Empty input shows error message")
        void emptyInputShowsError() {
            runMenuWithInput("\n0\n");
            assertTrue(output.toString().contains("Invalid option"));
        }

        @Test
        @DisplayName("Text input shows error message")
        void textInputShowsError() {
            runMenuWithInput("abc\n0\n");
            assertTrue(output.toString().contains("Invalid option"));
        }
    }
}