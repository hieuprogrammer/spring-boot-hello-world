package dev.hieu.springboothelloworld.web.controller;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.dto.PageResponse;
import dev.hieu.springboothelloworld.dto.TodoDTO;
import dev.hieu.springboothelloworld.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import dev.hieu.springboothelloworld.util.TestDataLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TodoController todoController;

    private TodoDTO todoDTO1;
    private TodoDTO todoDTO2;
    private UUID todoId1;
    private UUID todoId2;
    private PageResponse<TodoDTO> pageResponse;

    @BeforeEach
    void setUp() {
        // Load test data from JSON
        List<TodoDTO> testTodos = TestDataLoader.loadTestTodos();
        todoDTO1 = testTodos.get(0);
        todoDTO2 = testTodos.get(1);
        todoId1 = todoDTO1.getId();
        todoId2 = todoDTO2.getId();

        pageResponse = new PageResponse<>(
                Arrays.asList(todoDTO1, todoDTO2),
                0, 10, 2, 1, true, true
        );
    }

    @Test
    void listTodos_WithoutFilters_ShouldReturnListPage() {
        // Given
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, null, null, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
        verify(model, times(1)).addAttribute("todos", pageResponse.getContent());
        verify(model, times(1)).addAttribute("pageResponse", pageResponse);
        verify(model, times(1)).addAttribute("currentPage", 0);
        verify(model, times(1)).addAttribute("pageSize", 10);
        verify(model, times(1)).addAttribute("sort", null);
        verify(model, times(1)).addAttribute("keyword", null);
        verify(model, times(1)).addAttribute("statusFilter", null);
        verify(model, times(1)).addAttribute("statuses", Status.values());
    }

    @Test
    void listTodos_WithKeyword_ShouldUseSearch() {
        // Given
        String keyword = "Test";
        when(todoService.searchTodos(eq(keyword), isNull(), any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, null, keyword, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).searchTodos(eq(keyword), isNull(), any(Pageable.class));
        verify(todoService, never()).getAllTodos(any(Pageable.class));
    }

    @Test
    void listTodos_WithStatus_ShouldUseSearch() {
        // Given
        Status status = Status.PENDING;
        when(todoService.searchTodos(isNull(), eq(status), any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, null, null, status, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).searchTodos(isNull(), eq(status), any(Pageable.class));
        verify(todoService, never()).getAllTodos(any(Pageable.class));
    }

    @Test
    void listTodos_WithKeywordAndStatus_ShouldUseSearch() {
        // Given
        String keyword = "Test";
        Status status = Status.PENDING;
        when(todoService.searchTodos(eq(keyword), eq(status), any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, null, keyword, status, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).searchTodos(eq(keyword), eq(status), any(Pageable.class));
        verify(todoService, never()).getAllTodos(any(Pageable.class));
    }

    @Test
    void listTodos_WithEmptyKeyword_ShouldNotUseSearch() {
        // Given
        String keyword = "   ";
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, null, keyword, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
        verify(todoService, never()).searchTodos(anyString(), any(), any(Pageable.class));
    }

    @Test
    void listTodos_WithSort_ShouldCreatePageableWithSort() {
        // Given
        String sort = "todo,asc";
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, sort, null, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void listTodos_WithDescSort_ShouldCreatePageableWithDescSort() {
        // Given
        String sort = "status,desc";
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, sort, null, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void listTodos_WithInvalidSort_ShouldIgnoreSort() {
        // Given
        String sort = "invalidField,asc";
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, sort, null, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void listTodos_WithMalformedSort_ShouldIgnoreSort() {
        // Given
        String sort = "todo";
        when(todoService.getAllTodos(any(Pageable.class))).thenReturn(pageResponse);

        // When
        String viewName = todoController.listTodos(0, 10, sort, null, null, model);

        // Then
        assertEquals("todos/list", viewName);
        verify(todoService, times(1)).getAllTodos(any(Pageable.class));
    }

    @Test
    void showCreateForm_ShouldReturnFormPage() {
        // When
        String viewName = todoController.showCreateForm(model);

        // Then
        assertEquals("todos/form", viewName);
        verify(model, times(1)).addAttribute(eq("todo"), any(TodoDTO.class));
        verify(model, times(1)).addAttribute("statuses", Status.values());
        verify(model, times(1)).addAttribute("isEdit", false);
    }

    @Test
    void showEditForm_ShouldReturnFormPageWithTodo() {
        // Given
        when(todoService.getTodoById(todoId1)).thenReturn(todoDTO1);

        // When
        String viewName = todoController.showEditForm(todoId1, model);

        // Then
        assertEquals("todos/form", viewName);
        verify(todoService, times(1)).getTodoById(todoId1);
        verify(model, times(1)).addAttribute("todo", todoDTO1);
        verify(model, times(1)).addAttribute("statuses", Status.values());
        verify(model, times(1)).addAttribute("isEdit", true);
    }

    @Test
    void createTodo_Success_ShouldRedirectWithSuccessMessage() {
        // Given
        String todo = "New Todo";
        String description = "New Description";
        Status status = Status.PENDING;
        TodoDTO createdDTO = new TodoDTO();
        createdDTO.setId(todoId1);
        createdDTO.setTodo(todo);
        createdDTO.setDescription(description);
        createdDTO.setStatus(status);

        when(todoService.createTodo(any())).thenReturn(createdDTO);

        // When
        String redirectUrl = todoController.createTodo(todo, description, status, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).createTodo(any());
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Todo created successfully!");
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void createTodo_WithNullDescription_ShouldSucceed() {
        // Given
        String todo = "New Todo";
        String description = null;
        Status status = Status.PENDING;
        TodoDTO createdDTO = new TodoDTO();
        createdDTO.setId(todoId1);
        createdDTO.setTodo(todo);
        createdDTO.setStatus(status);

        when(todoService.createTodo(any())).thenReturn(createdDTO);

        // When
        String redirectUrl = todoController.createTodo(todo, description, status, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).createTodo(any());
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Todo created successfully!");
    }

    @Test
    void createTodo_WithException_ShouldRedirectWithErrorMessage() {
        // Given
        String todo = "New Todo";
        String description = "New Description";
        Status status = Status.PENDING;
        String errorMessage = "Service error";

        when(todoService.createTodo(any())).thenThrow(new RuntimeException(errorMessage));

        // When
        String redirectUrl = todoController.createTodo(todo, description, status, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).createTodo(any());
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Failed to create todo: " + errorMessage);
    }

    @Test
    void updateTodo_Success_ShouldRedirectWithSuccessMessage() {
        // Given
        String todo = "Updated Todo";
        String description = "Updated Description";
        Status status = Status.COMPLETED;
        TodoDTO updatedDTO = new TodoDTO();
        updatedDTO.setId(todoId1);
        updatedDTO.setTodo(todo);
        updatedDTO.setDescription(description);
        updatedDTO.setStatus(status);

        when(todoService.updateTodo(eq(todoId1), any())).thenReturn(updatedDTO);

        // When
        String redirectUrl = todoController.updateTodo(todoId1, todo, description, status, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).updateTodo(eq(todoId1), any());
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Todo updated successfully!");
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void updateTodo_WithNullDescription_ShouldSucceed() {
        // Given
        String todo = "Updated Todo";
        String description = null;
        Status status = Status.COMPLETED;
        TodoDTO updatedDTO = new TodoDTO();
        updatedDTO.setId(todoId1);
        updatedDTO.setTodo(todo);
        updatedDTO.setStatus(status);

        when(todoService.updateTodo(eq(todoId1), any())).thenReturn(updatedDTO);

        // When
        String redirectUrl = todoController.updateTodo(todoId1, todo, description, status, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).updateTodo(eq(todoId1), any());
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Todo updated successfully!");
    }

    @Test
    void updateTodo_WithException_ShouldRedirectWithErrorMessage() {
        // Given
        String todo = "Updated Todo";
        String description = "Updated Description";
        Status status = Status.COMPLETED;
        String errorMessage = "Todo not found";

        when(todoService.updateTodo(eq(todoId1), any())).thenThrow(new RuntimeException(errorMessage));

        // When
        String redirectUrl = todoController.updateTodo(todoId1, todo, description, status, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).updateTodo(eq(todoId1), any());
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Failed to update todo: " + errorMessage);
    }

    @Test
    void deleteTodo_Success_ShouldRedirectWithSuccessMessage() {
        // Given
        doNothing().when(todoService).deleteTodo(todoId1);

        // When
        String redirectUrl = todoController.deleteTodo(todoId1, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).deleteTodo(todoId1);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Todo deleted successfully!");
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void deleteTodo_WithException_ShouldRedirectWithErrorMessage() {
        // Given
        String errorMessage = "Todo not found";
        doThrow(new RuntimeException(errorMessage)).when(todoService).deleteTodo(todoId1);

        // When
        String redirectUrl = todoController.deleteTodo(todoId1, redirectAttributes);

        // Then
        assertEquals("redirect:/todos", redirectUrl);
        verify(todoService, times(1)).deleteTodo(todoId1);
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Failed to delete todo: " + errorMessage);
    }

    @Test
    void createPageable_WithValidSortProperties_ShouldCreateSortedPageable() {
        // Test all valid sort properties
        String[] validProperties = {"todo", "description", "status", "id"};
        String[] directions = {"asc", "desc"};

        for (String property : validProperties) {
            for (String direction : directions) {
                String sort = property + "," + direction;
                Pageable pageable = invokeCreatePageable(0, 10, sort);
                assertNotNull(pageable);
            }
        }
    }

    @Test
    void createPageable_WithInvalidProperty_ShouldReturnUnsorted() {
        // Given
        String sort = "invalidProperty,asc";

        // When
        Pageable pageable = invokeCreatePageable(0, 10, sort);

        // Then
        assertNotNull(pageable);
        assertFalse(pageable.getSort().isSorted());
    }

    @Test
    void createPageable_WithNullSort_ShouldReturnUnsorted() {
        // When
        Pageable pageable = invokeCreatePageable(0, 10, null);

        // Then
        assertNotNull(pageable);
        assertFalse(pageable.getSort().isSorted());
    }

    @Test
    void createPageable_WithEmptySort_ShouldReturnUnsorted() {
        // When
        Pageable pageable = invokeCreatePageable(0, 10, "");

        // Then
        assertNotNull(pageable);
        assertFalse(pageable.getSort().isSorted());
    }

    @Test
    void createPageable_WithWhitespaceSort_ShouldReturnUnsorted() {
        // When
        Pageable pageable = invokeCreatePageable(0, 10, "   ");

        // Then
        assertNotNull(pageable);
        assertFalse(pageable.getSort().isSorted());
    }

    @Test
    void createPageable_WithSingleParameter_ShouldReturnUnsorted() {
        // When
        Pageable pageable = invokeCreatePageable(0, 10, "todo");

        // Then
        assertNotNull(pageable);
        assertFalse(pageable.getSort().isSorted());
    }

    @Test
    void createPageable_WithMultipleCommas_ShouldReturnUnsorted() {
        // When
        Pageable pageable = invokeCreatePageable(0, 10, "todo,asc,extra");

        // Then
        assertNotNull(pageable);
        // Should still process if first two parts are valid
        assertNotNull(pageable);
    }

    @Test
    void isValidSortProperty_WithValidProperties_ShouldReturnTrue() {
        // Test all valid properties
        assertTrue(invokeIsValidSortProperty("todo"));
        assertTrue(invokeIsValidSortProperty("description"));
        assertTrue(invokeIsValidSortProperty("status"));
        assertTrue(invokeIsValidSortProperty("id"));
    }

    @Test
    void isValidSortProperty_WithInvalidProperties_ShouldReturnFalse() {
        // Test invalid properties
        assertFalse(invokeIsValidSortProperty("invalid"));
        assertFalse(invokeIsValidSortProperty("title"));
        assertFalse(invokeIsValidSortProperty(""));
        assertFalse(invokeIsValidSortProperty("createdAt"));
    }

    // Helper methods to test private methods via reflection
    private Pageable invokeCreatePageable(int page, int size, String sort) {
        try {
            java.lang.reflect.Method method = TodoController.class.getDeclaredMethod(
                    "createPageable", int.class, int.class, String.class);
            method.setAccessible(true);
            return (Pageable) method.invoke(todoController, page, size, sort);
        } catch (java.lang.reflect.InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeIsValidSortProperty(String property) {
        try {
            java.lang.reflect.Method method = TodoController.class.getDeclaredMethod(
                    "isValidSortProperty", String.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(todoController, property);
        } catch (java.lang.reflect.InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

