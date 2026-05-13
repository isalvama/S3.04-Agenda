package org.agenda.task.service;

import org.agenda.task.dto.TaskRequest;
import org.agenda.task.dto.TaskResponse;
import org.agenda.task.exception.TaskNotFoundException;
import org.agenda.task.model.Priority;
import org.agenda.task.model.Status;
import org.agenda.task.model.Task;
import org.agenda.task.repository.TaskRepository;
import org.agenda.task.service.strategy.TaskStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    private TaskRepository repository;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(TaskRepository.class);
        service = new TaskServiceImpl(repository);
    }

    private Task createSampleTask() {
        Task task = new Task("Buy groceries", "Milk, eggs, bread", Status.PENDING,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7), null);
        task.setId(1L);
        return task;
    }

    private TaskRequest createValidRequest() {
        return new TaskRequest("Buy groceries", "Milk, eggs, bread", null,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7), null);
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPath {

        @Test
        @DisplayName("create: valid request returns TaskResponse with id")
        void createTaskWithValidRequest() {
            Task savedTask = createSampleTask();
            when(repository.save(any(Task.class))).thenReturn(savedTask);

            TaskResponse response = service.create(createValidRequest());

            assertAll("Task Response Validation",
                    () -> assertNotNull(response),
                    () -> assertEquals(1L, response.id()),
                    () -> assertEquals("Buy groceries", response.title()),
                    () -> assertEquals(Status.PENDING, response.status())
            );
            verify(repository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("getById: existing id returns TaskResponse")
        void getByIdReturnsTask() {
            Task task = createSampleTask();
            when(repository.findById(1L)).thenReturn(Optional.of(task));

            TaskResponse response = service.getById(1L);

            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("Buy groceries", response.title());
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("getAll: returns list of all tasks")
        void getAllReturnsList() {
            Task task1 = createSampleTask();
            Task task2 = new Task("Clean house", "Kitchen and  bathroom", Status.PENDING,
                    Priority.LOW, LocalDateTime.now().plusDays(3), null);
            task2.setId(2L);
            when(repository.findAll()).thenReturn(List.of(task1, task2));

            List<TaskResponse> result = service.getAll();

            assertEquals(2, result.size());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("getPendingTasks: returns only pending tasks")
        void getPendingTasksReturnsPending() {
            Task task = createSampleTask();
            when(repository.findAllByStatus(Status.PENDING)).thenReturn(List.of(task));

            List<TaskResponse> result = service.getPendingTasks();

            assertEquals(1, result.size());
            assertEquals(Status.PENDING, result.get(0).status());
            verify(repository, times(1)).findAllByStatus(Status.PENDING);
        }

        @Test
        @DisplayName("getCompletedTasks: returns only completed tasks")
        void getCompletedTasksReturnsDone() {
            Task task = createSampleTask();
            task.setStatus(Status.DONE);
            when(repository.findAllByStatus(Status.DONE)).thenReturn(List.of(task));

            List<TaskResponse> result = service.getCompletedTasks();

            assertEquals(1, result.size());
            assertEquals(Status.DONE, result.get(0).status());
            verify(repository, times(1)).findAllByStatus(Status.DONE);
        }

        @Test
        @DisplayName("markAsDone: changes task status to DONE")
        void markAsDoneChangesStatus() {
            Task task = createSampleTask();
            when(repository.findById(1L)).thenReturn(Optional.of(task));
            when(repository.save(any(Task.class))).thenReturn(task);

            TaskResponse response = service.markAsDone(1L);

            assertEquals(Status.DONE, response.status());
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("update: valid request updates and returns TaskResponse")
        void updateTaskWithValidRequest() {
            Task existingTask = createSampleTask();
            when(repository.findById(1L)).thenReturn(Optional.of(existingTask));
            when(repository.save(any(Task.class))).thenReturn(existingTask);

            TaskRequest updatedRequest = new TaskRequest("Updated title", "Update body",
                    Status.IN_PROGRESS, Priority.HIGH, LocalDateTime.now().plusDays(14), null);

            TaskResponse response = service.update(1L, updatedRequest);

            assertNotNull(response);
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("delete: existing task returns true")
        void deleteExistingTask() {
            when(repository.existsById(1L)).thenReturn(true);
            when(repository.deleteById(1L)).thenReturn(true);

            boolean result = service.delete(1L);

            assertTrue(result);
            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("listTasks: delegates execution to the strategy")
        void listTaskDelegatesToStrategy() {
            TaskStrategy mockStrategy = mock(TaskStrategy.class);
            List<TaskResponse> expectedResponse = List.of(mock(TaskResponse.class));
            when(mockStrategy.execute()).thenReturn(expectedResponse);

            List<TaskResponse> result = service.listTasks(mockStrategy);

            assertEquals(expectedResponse, result);
            verify(mockStrategy, times(1)).execute();
        }
    }

        @Nested
        @DisplayName("Unhappy Path Tests")
        class UnhappyPath {

            @Test
            @DisplayName("create: null request throws NullPointerException")
            void createWithNullRequestThrows() {
                assertThrows(NullPointerException.class, () -> service.create(null));
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create: null title throws IllegalArgumentException")
            void createWithNullTitleThrows() {
                TaskRequest request = new TaskRequest(null, "body", null, null,
                        LocalDateTime.now().plusDays(7), null);

                assertThrows(IllegalArgumentException.class, () -> service.create(request));
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create: blank title throws IllegalArgumentException")
            void createWithBlankTitleThrows() {
                TaskRequest request = new TaskRequest("  ", "body", null, null,
                        LocalDateTime.now().plusDays(7), null);

                assertThrows(IllegalArgumentException.class, () -> service.create(request));
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("getById: null id throws NullPointerException")
            void getByIdWithNullIdThrows() {
                assertThrows(NullPointerException.class, () -> service.getById(null));
                verify(repository, never()).findById(any());
            }

            @Test
            @DisplayName("getById: non-existent id throws TaskNotFoundException")
            void getByIdNotFoundThrows() {
                when(repository.findById(99L)).thenReturn(Optional.empty());

                TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () -> service.getById(99L));
                assertTrue(ex.getMessage().contains("99"));
                verify(repository, times(1)).findById(99L);
            }

            @Test
            @DisplayName("markAsDone: null id throws NullPointerException")
            void markAsDoneWithNullIdThrows() {
                assertThrows(NullPointerException.class, () -> service.markAsDone(null));
                verify(repository, never()).findById(any());
            }

            @Test
            @DisplayName("markAsDone: non-existent id throws TaskNotFoundException")
            void markAsDoneNotFoundThrows() {
                when(repository.findById(99L)).thenReturn(Optional.empty());

                assertThrows(TaskNotFoundException.class, () -> service.markAsDone(99L));
                verify(repository, times(1)).findById(99L);
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("delete: null id throws NullPointerException")
            void deleteNotFoundThrows() {
                when(repository.existsById(99L)).thenReturn(false);

                assertThrows(TaskNotFoundException.class, () -> service.delete(99L));
                verify(repository, times(1)).existsById(99L);
                verify(repository, never()).deleteById(any());
            }

            @Test
            @DisplayName("update: non-existent id throws TaskNotFoundException")
            void updateNotFoundThrows() {
                when(repository.findById(99L)).thenReturn(Optional.empty());

                TaskRequest request = createValidRequest();
                assertThrows(TaskNotFoundException.class, () -> service.update(99L, request));
                verify(repository, times(1)).findById(99L);
                verify(repository, never()).save(any());
            }
        }
    }