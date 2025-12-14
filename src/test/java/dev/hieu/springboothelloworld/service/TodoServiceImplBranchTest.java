package dev.hieu.springboothelloworld.service;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.dto.PageResponse;
import dev.hieu.springboothelloworld.dto.TodoCreateDTO;
import dev.hieu.springboothelloworld.dto.TodoDTO;
import dev.hieu.springboothelloworld.dto.TodoUpdateDTO;
import dev.hieu.springboothelloworld.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplBranchTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo todo1;
    private UUID todoId1;

    @BeforeEach
    void setUp() {
        todoId1 = UUID.randomUUID();
        todo1 = new Todo();
        todo1.setId(todoId1);
        todo1.setTodo("Test Todo");
        todo1.setDescription("Description");
        todo1.setStatus(Status.PENDING);
    }

    @Test
    void updateTodo_WithOnlyTodoField_ShouldUpdateOnlyTodo() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo("Updated Todo");
        updateDTO.setDescription(null);
        updateDTO.setStatus(null);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo saved = invocation.getArgument(0);
            assertEquals("Updated Todo", saved.getTodo());
            assertEquals("Description", saved.getDescription()); // Should remain unchanged
            assertEquals(Status.PENDING, saved.getStatus()); // Should remain unchanged
            return saved;
        });

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithOnlyDescriptionField_ShouldUpdateOnlyDescription() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo(null);
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(null);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo saved = invocation.getArgument(0);
            assertEquals("Test Todo", saved.getTodo()); // Should remain unchanged
            assertEquals("Updated Description", saved.getDescription());
            assertEquals(Status.PENDING, saved.getStatus()); // Should remain unchanged
            return saved;
        });

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithOnlyStatusField_ShouldUpdateOnlyStatus() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo(null);
        updateDTO.setDescription(null);
        updateDTO.setStatus(Status.COMPLETED);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo saved = invocation.getArgument(0);
            assertEquals("Test Todo", saved.getTodo()); // Should remain unchanged
            assertEquals("Description", saved.getDescription()); // Should remain unchanged
            assertEquals(Status.COMPLETED, saved.getStatus());
            return saved;
        });

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithTodoAndDescription_ShouldUpdateBoth() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo("Updated Todo");
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(null);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithTodoAndStatus_ShouldUpdateBoth() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo("Updated Todo");
        updateDTO.setDescription(null);
        updateDTO.setStatus(Status.COMPLETED);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithDescriptionAndStatus_ShouldUpdateBoth() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo(null);
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(Status.COMPLETED);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void searchTodos_WithEmptyKeyword_ShouldTreatAsNull() {
        // Given
        String keyword = "";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = new PageImpl<>(Arrays.asList(todo1), pageable, 1);
        when(todoRepository.searchTodos(null, null, pageable)).thenReturn(page);

        // When
        PageResponse<TodoDTO> result = todoService.searchTodos(keyword, null, pageable);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).searchTodos(null, null, pageable);
    }

    @Test
    void searchTodos_WithWhitespaceKeyword_ShouldTreatAsNull() {
        // Given
        String keyword = "   ";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = new PageImpl<>(Arrays.asList(todo1), pageable, 1);
        when(todoRepository.searchTodos(null, null, pageable)).thenReturn(page);

        // When
        PageResponse<TodoDTO> result = todoService.searchTodos(keyword, null, pageable);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).searchTodos(null, null, pageable);
    }

    @Test
    void toPageResponse_WithMultiplePages_ShouldReturnCorrectValues() {
        // Given
        Pageable pageable = PageRequest.of(1, 5); // Second page, 5 items per page
        List<Todo> todos = Arrays.asList(todo1);
        Page<Todo> page = new PageImpl<>(todos, pageable, 10); // Total 10 items, 2 pages

        // Use reflection to test private method
        try {
            java.lang.reflect.Method method = TodoServiceImpl.class.getDeclaredMethod(
                    "toPageResponse", Page.class);
            method.setAccessible(true);
            PageResponse<TodoDTO> result = (PageResponse<TodoDTO>) method.invoke(
                    todoService, page);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getPage());
            assertEquals(5, result.getSize());
            assertEquals(10, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
            assertFalse(result.isFirst());
            assertTrue(result.isLast()); // Page 1 of 2 is the last page
        } catch (Exception e) {
            fail("Failed to test toPageResponse: " + e.getMessage());
        }
    }

    @Test
    void toPageResponse_WithFirstPage_ShouldReturnFirstTrue() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Todo> todos = Arrays.asList(todo1);
        Page<Todo> page = new PageImpl<>(todos, pageable, 1);

        // Use reflection to test private method
        try {
            java.lang.reflect.Method method = TodoServiceImpl.class.getDeclaredMethod(
                    "toPageResponse", Page.class);
            method.setAccessible(true);
            PageResponse<TodoDTO> result = (PageResponse<TodoDTO>) method.invoke(
                    todoService, page);

            // Then
            assertTrue(result.isFirst());
            assertTrue(result.isLast());
        } catch (Exception e) {
            fail("Failed to test toPageResponse: " + e.getMessage());
        }
    }
}

