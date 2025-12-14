package dev.hieu.springboothelloworld.configuration;

import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.repository.TodoRepository;
import dev.hieu.springboothelloworld.util.TestDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_WhenDatabaseIsEmpty_ShouldInitializeMockData() throws Exception {
        // Given
        List<Todo> mockTodos = TestDataLoader.loadMockTodos();
        when(todoRepository.count()).thenReturn(0L);
        when(todoRepository.saveAll(anyList())).thenReturn(mockTodos);

        // When
        dataInitializer.run();

        // Then
        verify(todoRepository, times(1)).count();
        verify(todoRepository, times(1)).saveAll(anyList());
    }

    @Test
    void run_WhenDatabaseHasData_ShouldSkipInitialization() throws Exception {
        // Given
        when(todoRepository.count()).thenReturn(5L);

        // When
        dataInitializer.run();

        // Then
        verify(todoRepository, times(1)).count();
        verify(todoRepository, never()).saveAll(anyList());
    }

    @Test
    void run_ShouldCreateCorrectMockTodos() throws Exception {
        // Given
        List<Todo> expectedTodos = TestDataLoader.loadMockTodos();
        when(todoRepository.count()).thenReturn(0L);
        when(todoRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Todo> todos = invocation.getArgument(0);
            // Verify that correct number of todos are created
            assertEquals(expectedTodos.size(), todos.size(), 
                    "Expected " + expectedTodos.size() + " todos, but got " + todos.size());
            // Verify first todo matches JSON data
            Todo firstTodo = todos.get(0);
            Todo expectedFirst = expectedTodos.get(0);
            assertEquals(expectedFirst.getTodo(), firstTodo.getTodo(), "First todo title mismatch");
            assertEquals(expectedFirst.getStatus(), firstTodo.getStatus(), "First todo status mismatch");
            assertEquals(expectedFirst.getDescription(), firstTodo.getDescription(), "First todo description mismatch");
            return todos;
        });

        // When
        dataInitializer.run();

        // Then
        verify(todoRepository, times(1)).saveAll(anyList());
    }
}

