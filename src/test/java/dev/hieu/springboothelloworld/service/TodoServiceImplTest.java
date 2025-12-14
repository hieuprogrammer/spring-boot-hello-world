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
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo todo1;
    private Todo todo2;
    private UUID todoId1;
    private UUID todoId2;

    @BeforeEach
    void setUp() {
        todoId1 = UUID.randomUUID();
        todoId2 = UUID.randomUUID();

        todo1 = new Todo();
        todo1.setId(todoId1);
        todo1.setTodo("Test Todo 1");
        todo1.setDescription("Description 1");
        todo1.setStatus(Status.PENDING);

        todo2 = new Todo();
        todo2.setId(todoId2);
        todo2.setTodo("Test Todo 2");
        todo2.setDescription("Description 2");
        todo2.setStatus(Status.IN_PROGRESS);
    }

    @Test
    void getAllTodos_ShouldReturnListOfTodos() {
        // Given
        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(todoRepository.findAll()).thenReturn(todos);

        // When
        List<TodoDTO> result = todoService.getAllTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(todoId1, result.get(0).getId());
        assertEquals("Test Todo 1", result.get(0).getTodo());
        assertEquals(Status.PENDING, result.get(0).getStatus());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getAllTodos_WithPageable_ShouldReturnPageResponse() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = new PageImpl<>(Arrays.asList(todo1, todo2), pageable, 2);
        when(todoRepository.findAll(pageable)).thenReturn(page);

        // When
        PageResponse<TodoDTO> result = todoService.getAllTodos(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        verify(todoRepository, times(1)).findAll(pageable);
    }

    @Test
    void searchTodos_WithKeyword_ShouldReturnFilteredTodos() {
        // Given
        String keyword = "Test";
        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(todoRepository.searchTodos(keyword, null)).thenReturn(todos);

        // When
        List<TodoDTO> result = todoService.searchTodos(keyword, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(todoRepository, times(1)).searchTodos(keyword, null);
    }

    @Test
    void searchTodos_WithStatus_ShouldReturnFilteredTodos() {
        // Given
        Status status = Status.PENDING;
        List<Todo> todos = Arrays.asList(todo1);
        when(todoRepository.searchTodos(null, status)).thenReturn(todos);

        // When
        List<TodoDTO> result = todoService.searchTodos(null, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Status.PENDING, result.get(0).getStatus());
        verify(todoRepository, times(1)).searchTodos(null, status);
    }

    @Test
    void searchTodos_WithEmptyKeyword_ShouldTreatAsNull() {
        // Given
        String keyword = "   ";
        List<Todo> todos = Arrays.asList(todo1);
        when(todoRepository.searchTodos(null, null)).thenReturn(todos);

        // When
        List<TodoDTO> result = todoService.searchTodos(keyword, null);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).searchTodos(null, null);
    }

    @Test
    void searchTodos_WithPageable_ShouldReturnPageResponse() {
        // Given
        String keyword = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = new PageImpl<>(Arrays.asList(todo1), pageable, 1);
        when(todoRepository.searchTodos(keyword, null, pageable)).thenReturn(page);

        // When
        PageResponse<TodoDTO> result = todoService.searchTodos(keyword, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(todoRepository, times(1)).searchTodos(keyword, null, pageable);
    }

    @Test
    void getTodoById_WhenExists_ShouldReturnTodo() {
        // Given
        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));

        // When
        TodoDTO result = todoService.getTodoById(todoId1);

        // Then
        assertNotNull(result);
        assertEquals(todoId1, result.getId());
        assertEquals("Test Todo 1", result.getTodo());
        assertEquals("Description 1", result.getDescription());
        assertEquals(Status.PENDING, result.getStatus());
        verify(todoRepository, times(1)).findById(todoId1);
    }

    @Test
    void getTodoById_WhenNotExists_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(todoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            todoService.getTodoById(nonExistentId);
        });

        assertEquals("Todo not found with id: '" + nonExistentId + "'", exception.getMessage());
        verify(todoRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createTodo_WithAllFields_ShouldCreateAndReturnTodo() {
        // Given
        TodoCreateDTO createDTO = new TodoCreateDTO();
        createDTO.setTodo("New Todo");
        createDTO.setDescription("New Description");
        createDTO.setStatus(Status.PENDING);

        Todo savedTodo = new Todo();
        savedTodo.setId(todoId1);
        savedTodo.setTodo("New Todo");
        savedTodo.setDescription("New Description");
        savedTodo.setStatus(Status.PENDING);

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        // When
        TodoDTO result = todoService.createTodo(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("New Todo", result.getTodo());
        assertEquals("New Description", result.getDescription());
        assertEquals(Status.PENDING, result.getStatus());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void createTodo_WithoutStatus_ShouldDefaultToPending() {
        // Given
        TodoCreateDTO createDTO = new TodoCreateDTO();
        createDTO.setTodo("New Todo");
        createDTO.setDescription("New Description");
        createDTO.setStatus(null);

        Todo savedTodo = new Todo();
        savedTodo.setId(todoId1);
        savedTodo.setTodo("New Todo");
        savedTodo.setDescription("New Description");
        savedTodo.setStatus(Status.PENDING);

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        // When
        TodoDTO result = todoService.createTodo(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithAllFields_ShouldUpdateAndReturnTodo() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo("Updated Todo");
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(Status.COMPLETED);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).findById(todoId1);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WithPartialFields_ShouldUpdateOnlyProvidedFields() {
        // Given
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setStatus(Status.COMPLETED);
        updateDTO.setTodo(null);
        updateDTO.setDescription(null);

        when(todoRepository.findById(todoId1)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        TodoDTO result = todoService.updateTodo(todoId1, updateDTO);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).findById(todoId1);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WhenNotExists_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo("Updated Todo");

        when(todoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            todoService.updateTodo(nonExistentId, updateDTO);
        });

        assertEquals("Todo not found with id: '" + nonExistentId + "'", exception.getMessage());
        verify(todoRepository, times(1)).findById(nonExistentId);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void deleteTodo_WhenExists_ShouldDeleteTodo() {
        // Given
        when(todoRepository.existsById(todoId1)).thenReturn(true);

        // When
        todoService.deleteTodo(todoId1);

        // Then
        verify(todoRepository, times(1)).existsById(todoId1);
        verify(todoRepository, times(1)).deleteById(todoId1);
    }

    @Test
    void deleteTodo_WhenNotExists_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(todoRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            todoService.deleteTodo(nonExistentId);
        });

        assertEquals("Todo not found with id: '" + nonExistentId + "'", exception.getMessage());
        verify(todoRepository, times(1)).existsById(nonExistentId);
        verify(todoRepository, never()).deleteById(any(UUID.class));
    }
}

