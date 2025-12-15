package dev.hieu.springboothelloworld.service;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.dto.PageResponse;
import dev.hieu.springboothelloworld.dto.TodoCreateDTO;
import dev.hieu.springboothelloworld.dto.TodoDTO;
import dev.hieu.springboothelloworld.dto.TodoUpdateDTO;
import dev.hieu.springboothelloworld.exception.ResourceNotFoundException;
import dev.hieu.springboothelloworld.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {
    
    private final TodoRepository todoRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<TodoDTO> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TodoDTO> getAllTodos(Pageable pageable) {
        Page<Todo> page = todoRepository.findAll(pageable);
        return toPageResponse(page);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TodoDTO> searchTodos(String keyword, Status status) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;
        return todoRepository.searchTodos(searchKeyword, status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TodoDTO> searchTodos(String keyword, Status status, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;
        Page<Todo> page = todoRepository.searchTodos(searchKeyword, status, pageable);
        return toPageResponse(page);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TodoDTO getTodoById(UUID id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        return toDTO(todo);
    }
    
    @Override
    public TodoDTO createTodo(TodoCreateDTO todoCreateDTO) {
        Todo todo = new Todo();
        todo.setTodo(todoCreateDTO.getTodo());
        todo.setDescription(todoCreateDTO.getDescription());
        todo.setStatus(todoCreateDTO.getStatus() != null ? todoCreateDTO.getStatus() : Status.PENDING);
        todo.setDueAt(todoCreateDTO.getDueAt());
        // createdAt and lastUpdatedAt are automatically set by @PrePersist
        
        Todo savedTodo = todoRepository.save(todo);
        return toDTO(savedTodo);
    }
    
    @Override
    public TodoDTO updateTodo(UUID id, TodoUpdateDTO todoUpdateDTO) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        
        if (todoUpdateDTO.getTodo() != null) {
            todo.setTodo(todoUpdateDTO.getTodo());
        }
        if (todoUpdateDTO.getDescription() != null) {
            todo.setDescription(todoUpdateDTO.getDescription());
        }
        if (todoUpdateDTO.getStatus() != null) {
            todo.setStatus(todoUpdateDTO.getStatus());
        }
        if (todoUpdateDTO.getDueAt() != null) {
            todo.setDueAt(todoUpdateDTO.getDueAt());
        }
        // lastUpdatedAt is automatically updated by @PreUpdate
        
        Todo updatedTodo = todoRepository.save(todo);
        return toDTO(updatedTodo);
    }
    
    @Override
    public void deleteTodo(UUID id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo", "id", id);
        }
        todoRepository.deleteById(id);
    }
    
    private TodoDTO toDTO(Todo todo) {
        return new TodoDTO(
                todo.getId(),
                todo.getTodo(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getLastUpdatedAt(),
                todo.getDueAt()
        );
    }
    
    private PageResponse<TodoDTO> toPageResponse(Page<Todo> page) {
        List<TodoDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}

