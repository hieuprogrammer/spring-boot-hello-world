package dev.hieu.springboothelloworld.web.api;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.dto.PageResponse;
import dev.hieu.springboothelloworld.dto.TodoCreateDTO;
import dev.hieu.springboothelloworld.dto.TodoDTO;
import dev.hieu.springboothelloworld.dto.TodoUpdateDTO;
import dev.hieu.springboothelloworld.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Todo Management", description = "APIs for managing todos with pagination, sorting, and filtering")
public class TodoApi {

    private final TodoService todoService;

    @Operation(
            summary = "Get all todos",
            description = "Retrieve all todos with optional pagination and sorting. Supports query parameters: page (default 0), size (default 10), sort (e.g., 'todo,asc' or 'status,desc')"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved todos",
                    content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<TodoDTO>> getAllTodos(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field and direction (e.g., 'todo,asc' or 'status,desc')", example = "todo,asc")
            @RequestParam(required = false) String sort) {

        Pageable pageable = createPageable(page, size, sort);
        PageResponse<TodoDTO> response = todoService.getAllTodos(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search todos",
            description = "Search todos by keyword and/or status with pagination and sorting. Supports query parameters: keyword, status, page (default 0), size (default 10), sort (e.g., 'todo,asc')"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
                    content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<TodoDTO>> searchTodos(
            @Parameter(description = "Search keyword for todo title or description", example = "Spring Boot")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by status", example = "PENDING")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field and direction (e.g., 'todo,asc' or 'status,desc')", example = "status,asc")
            @RequestParam(required = false) String sort) {

        Pageable pageable = createPageable(page, size, sort);
        PageResponse<TodoDTO> response = todoService.searchTodos(keyword, status, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get todo by ID",
            description = "Retrieve a specific todo by its UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved todo",
                    content = @Content(schema = @Schema(implementation = TodoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(
            @Parameter(description = "Todo UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        TodoDTO todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    @Operation(
            summary = "Create a new todo",
            description = "Create a new todo item"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Todo created successfully",
                    content = @Content(schema = @Schema(implementation = TodoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(
            @Parameter(description = "Todo creation data")
            @Valid @RequestBody TodoCreateDTO todoCreateDTO) {
        TodoDTO createdTodo = todoService.createTodo(todoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @Operation(
            summary = "Update a todo",
            description = "Update an existing todo by its UUID. All fields are optional - only provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo updated successfully",
                    content = @Content(schema = @Schema(implementation = TodoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(
            @Parameter(description = "Todo UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Parameter(description = "Todo update data")
            @Valid @RequestBody TodoUpdateDTO todoUpdateDTO) {
        TodoDTO updatedTodo = todoService.updateTodo(id, todoUpdateDTO);
        return ResponseEntity.ok(updatedTodo);
    }

    @Operation(
            summary = "Delete a todo",
            description = "Delete a todo by its UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "Todo UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort sortObj = Sort.unsorted();

        if (sort != null && !sort.trim().isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0].trim();
                String direction = sortParams[1].trim().toUpperCase();

                if (isValidSortProperty(property)) {
                    Sort.Direction sortDirection = direction.equals("DESC")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;
                    sortObj = Sort.by(sortDirection, property);
                }
            }
        }

        return PageRequest.of(page, size, sortObj);
    }

    private boolean isValidSortProperty(String property) {
        return property.equals("todo") ||
               property.equals("description") ||
               property.equals("status") ||
               property.equals("id");
    }
}
