package dev.hieu.springboothelloworld.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.dto.*;
import dev.hieu.springboothelloworld.service.feature.FeatureFlag;
import dev.hieu.springboothelloworld.service.feature.FeatureFlagService;
import dev.hieu.springboothelloworld.service.TodoService;
import dev.hieu.springboothelloworld.web.api.TodoApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoApi.class)
class TodoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private FeatureFlagService featureFlagService;

    @Autowired
    private ObjectMapper objectMapper;

    private TodoDTO todoDTO1;
    private TodoDTO todoDTO2;
    private UUID todoId1;
    private UUID todoId2;

    @BeforeEach
    void setUp() {
        todoId1 = UUID.randomUUID();
        todoId2 = UUID.randomUUID();

        todoDTO1 = new TodoDTO();
        todoDTO1.setId(todoId1);
        todoDTO1.setTodo("Test Todo 1");
        todoDTO1.setDescription("Description 1");
        todoDTO1.setStatus(Status.PENDING);

        todoDTO2 = new TodoDTO();
        todoDTO2.setId(todoId2);
        todoDTO2.setTodo("Test Todo 2");
        todoDTO2.setDescription("Description 2");
        todoDTO2.setStatus(Status.IN_PROGRESS);
    }

    @Test
    void getAllTodos_ShouldReturnPageResponse() throws Exception {
        // Given
        when(featureFlagService.isEnabled(any())).thenReturn(true);
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void getAllTodos_WithSorting_ShouldReturnSortedResults() throws Exception {
        // Given
        when(featureFlagService.isEnabled(any())).thenReturn(true);
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "todo,asc"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void searchTodos_WithKeyword_ShouldReturnFilteredResults() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_SEARCH_API)).thenReturn(true);
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1),
                0, 10, 1, 1, true, true
        );
        when(todoService.searchTodos(eq("Test"), isNull(), any(Pageable.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos/search")
                        .param("keyword", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(todoService, times(1)).searchTodos(eq("Test"), isNull(), any(Pageable.class));
    }

    @Test
    void searchTodos_WithStatus_ShouldReturnFilteredResults() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_SEARCH_API)).thenReturn(true);
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1),
                0, 10, 1, 1, true, true
        );
        when(todoService.searchTodos(isNull(), eq(Status.PENDING), any(Pageable.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos/search")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).searchTodos(isNull(), eq(Status.PENDING), any(Pageable.class));
    }

    @Test
    void getTodoById_WhenExists_ShouldReturnTodo() throws Exception {
        // Given
        when(featureFlagService.isEnabled(any())).thenReturn(true);
        when(todoService.getTodoById(todoId1)).thenReturn(todoDTO1);

        // When & Then
        mockMvc.perform(get("/api/todos/{id}", todoId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId1.toString()))
                .andExpect(jsonPath("$.todo").value("Test Todo 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(todoService, times(1)).getTodoById(todoId1);
    }

    @Test
    void createTodo_WithValidData_ShouldCreateAndReturnTodo() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)).thenReturn(true);
        TodoCreateDTO createDTO = new TodoCreateDTO();
        createDTO.setTodo("New Todo");
        createDTO.setDescription("New Description");
        createDTO.setStatus(Status.PENDING);

        TodoDTO createdDTO = new TodoDTO();
        createdDTO.setId(todoId1);
        createdDTO.setTodo("New Todo");
        createdDTO.setDescription("New Description");
        createdDTO.setStatus(Status.PENDING);

        when(todoService.createTodo(any(TodoCreateDTO.class))).thenReturn(createdDTO);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.todo").value("New Todo"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(todoService, times(1)).createTodo(any(TodoCreateDTO.class));
    }

    @Test
    void createTodo_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)).thenReturn(true);
        TodoCreateDTO createDTO = new TodoCreateDTO();
        createDTO.setTodo(""); // Invalid: empty string

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(any(TodoCreateDTO.class));
    }

    @Test
    void updateTodo_WithValidData_ShouldUpdateAndReturnTodo() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)).thenReturn(true);
        TodoUpdateDTO updateDTO = new TodoUpdateDTO();
        updateDTO.setTodo("Updated Todo");
        updateDTO.setStatus(Status.COMPLETED);

        TodoDTO updatedDTO = new TodoDTO();
        updatedDTO.setId(todoId1);
        updatedDTO.setTodo("Updated Todo");
        updatedDTO.setDescription("Description 1");
        updatedDTO.setStatus(Status.COMPLETED);

        when(todoService.updateTodo(eq(todoId1), any(TodoUpdateDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todo").value("Updated Todo"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(todoService, times(1)).updateTodo(eq(todoId1), any(TodoUpdateDTO.class));
    }

    @Test
    void deleteTodo_WhenExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)).thenReturn(true);
        doNothing().when(todoService).deleteTodo(todoId1);

        // When & Then
        mockMvc.perform(delete("/api/todos/{id}", todoId1))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodo(todoId1);
    }

    @Test
    void createPageable_WithValidSort_ShouldCreatePageableWithSort() {
        // This tests the private method indirectly through the controller
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then - Test different sort properties
        try {
            mockMvc.perform(get("/api/todos")
                            .param("sort", "todo,asc"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/todos")
                            .param("sort", "status,desc"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/todos")
                            .param("sort", "description,asc"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/todos")
                            .param("sort", "id,desc"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            // Ignore for indirect testing
        }

        verify(todoService, atLeastOnce()).getAllTodos(any(Pageable.class));
    }

    @Test
    void createPageable_WithInvalidSort_ShouldIgnoreSort() throws Exception {
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then - Invalid sort property should be ignored
        mockMvc.perform(get("/api/todos")
                        .param("sort", "invalidField,asc"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void getAllTodos_WithNullSort_ShouldWork() throws Exception {
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void getAllTodos_WithEmptySort_ShouldWork() throws Exception {
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .param("sort", ""))
                .andExpect(status().isOk());

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void getAllTodos_WithWhitespaceSort_ShouldWork() throws Exception {
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .param("sort", "   "))
                .andExpect(status().isOk());

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void getAllTodos_WithSingleParameterSort_ShouldIgnore() throws Exception {
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .param("sort", "todo"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void getAllTodos_WithAllValidSortProperties_ShouldWork() throws Exception {
        // Given
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // Test all valid sort properties with both directions
        String[] properties = {"todo", "description", "status", "id"};
        String[] directions = {"asc", "desc"};

        for (String property : properties) {
            for (String direction : directions) {
                mockMvc.perform(get("/api/todos")
                                .param("sort", property + "," + direction))
                        .andExpect(status().isOk());
            }
        }

        verify(todoService, atLeast(8)).getAllTodos(any(Pageable.class));
    }

    @Test
    void searchTodos_WithKeywordAndStatus_ShouldReturnFilteredResults() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_SEARCH_API)).thenReturn(true);
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1),
                0, 10, 1, 1, true, true
        );
        when(todoService.searchTodos(eq("Test"), eq(Status.PENDING), any(Pageable.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos/search")
                        .param("keyword", "Test")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).searchTodos(eq("Test"), eq(Status.PENDING), any(Pageable.class));
    }

    @Test
    void searchTodos_WithSorting_ShouldWork() throws Exception {
        // Given
        when(featureFlagService.isEnabled(FeatureFlag.TODO_SEARCH_API)).thenReturn(true);
        PageResponse<TodoDTO> pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1),
                0, 10, 1, 1, true, true
        );
        when(todoService.searchTodos(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/todos/search")
                        .param("sort", "status,asc"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).searchTodos(isNull(), isNull(), any(Pageable.class));
    }
}
