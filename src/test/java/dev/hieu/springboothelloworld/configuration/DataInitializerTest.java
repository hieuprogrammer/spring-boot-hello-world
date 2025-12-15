package dev.hieu.springboothelloworld.configuration;

import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.repository.TodoRepository;
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
        when(todoRepository.count()).thenReturn(0L);
        // Mock saveAll to return the input list (for batching)
        when(todoRepository.saveAll(anyList())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            List<Todo> todos = invocation.getArgument(0);
            return new java.util.ArrayList<>(todos);
        });

        // When
        dataInitializer.run();

        // Then
        verify(todoRepository, times(1)).count();
        // With batching (1000 per batch), we expect multiple saveAll calls
        // For test CSV with 8 records, it should be 1 call
        // For production CSV with 10000 records, it would be 10 calls
        verify(todoRepository, atLeastOnce()).saveAll(anyList());
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
        when(todoRepository.count()).thenReturn(0L);
        final List<Todo> allSavedTodos = new java.util.ArrayList<>();
        
        when(todoRepository.saveAll(anyList())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            List<Todo> todos = invocation.getArgument(0);
            allSavedTodos.addAll(todos);
            return new java.util.ArrayList<>(todos);
        });

        // When
        dataInitializer.run();

        // Then
        // Verify that todos were loaded (either from CSV or generated)
        assertFalse(allSavedTodos.isEmpty(), "Should have loaded some todos");
        // Verify that todos have required fields
        for (Todo todo : allSavedTodos) {
            assertNotNull(todo.getTodo(), "Todo title should not be null");
            assertNotNull(todo.getStatus(), "Todo status should not be null");
        }
        // Verify saveAll was called at least once (could be multiple times for batching)
        verify(todoRepository, atLeastOnce()).saveAll(anyList());
    }
}

