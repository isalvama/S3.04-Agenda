package org.agenda;

import org.agenda.app.App;
import org.agenda.event.controller.EventController;
import org.agenda.event.controller.EventNotifier;
import org.agenda.event.service.EventRecurringService;
import org.agenda.note.controller.NoteController;
import org.agenda.shared.config.DatabaseConnection;
import org.agenda.task.controller.TaskController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppTest {

    @Mock
    private TaskController taskController;
    @Mock
    private NoteController noteController;
    @Mock
    private EventController eventController;

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
        App.showMainMenu(scanner, taskController, noteController, eventController);
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
            verify(eventController, times(1)).showMenu();
        }

        @Test
        @DisplayName("Option 0 exits with goodbye message")
        void option0ExitsWithGoodbye() {
            runMenuWithInput("0\n");
            assertTrue(output.toString().contains("Goodbye!"));
        }

        @Test
        void run_ShouldInvokeNotifierAndRecurringService() throws SQLException {
            String input = "0\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            try (MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {
                DatabaseConnection dbInstance = mock(DatabaseConnection.class);
                Connection fakeConnection = mock(Connection.class);

                mockedDb.when(DatabaseConnection::getInstance).thenReturn(dbInstance);
                when(dbInstance.getConnection()).thenReturn(fakeConnection);

                try (MockedConstruction<EventNotifier> notifierMocked = mockConstruction(EventNotifier.class);
                     MockedConstruction<EventRecurringService> recurringMocked = mockConstruction(EventRecurringService.class)) {

                    App.run();

                    EventNotifier capturedNotifier = notifierMocked.constructed().get(0);
                    verify(capturedNotifier, times(1)).notifyAllEvents();

                    EventRecurringService capturedRecurring = recurringMocked.constructed().get(0);

                    verify(capturedRecurring, timeout(1000).atLeastOnce()).processRecurringEvents();
                }
            }
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