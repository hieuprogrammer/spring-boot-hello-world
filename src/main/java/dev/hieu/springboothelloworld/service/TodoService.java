package dev.hieu.springboothelloworld.service;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.dto.PageResponse;
import dev.hieu.springboothelloworld.dto.TodoCreateDTO;
import dev.hieu.springboothelloworld.dto.TodoDTO;
import dev.hieu.springboothelloworld.dto.TodoUpdateDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TodoService {
    
    List<TodoDTO> getAllTodos();
    
    PageResponse<TodoDTO> getAllTodos(Pageable pageable);
    
    List<TodoDTO> searchTodos(String keyword, Status status);
    
    PageResponse<TodoDTO> searchTodos(String keyword, Status status, Pageable pageable);
    
    TodoDTO getTodoById(UUID id);
    
    TodoDTO createTodo(TodoCreateDTO todoCreateDTO);
    
    TodoDTO updateTodo(UUID id, TodoUpdateDTO todoUpdateDTO);
    
    void deleteTodo(UUID id);
}

