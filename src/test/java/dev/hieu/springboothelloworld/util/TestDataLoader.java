package dev.hieu.springboothelloworld.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.dto.TodoDTO;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestDataLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Todo> loadMockTodos() {
        try {
            InputStream inputStream = TestDataLoader.class
                    .getClassLoader()
                    .getResourceAsStream("test-data/mock-todos.json");
            
            List<MockTodoData> dataList = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<List<MockTodoData>>() {}
            );
            
            return dataList.stream()
                    .map(data -> {
                        Todo todo = new Todo();
                        todo.setTodo(data.todo);
                        todo.setDescription(data.description);
                        todo.setStatus(Status.valueOf(data.status));
                        return todo;
                    })
                    .collect(Collectors.toList());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to load mock todos from JSON", e);
        }
    }

    public static List<TodoDTO> loadTestTodos() {
        try {
            InputStream inputStream = TestDataLoader.class
                    .getClassLoader()
                    .getResourceAsStream("test-data/test-todos.json");
            
            List<TestTodoData> dataList = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<List<TestTodoData>>() {}
            );
            
            return dataList.stream()
                    .map(data -> {
                        TodoDTO dto = new TodoDTO();
                        dto.setId(UUID.fromString(data.id));
                        dto.setTodo(data.todo);
                        dto.setDescription(data.description);
                        dto.setStatus(Status.valueOf(data.status));
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to load test todos from JSON", e);
        }
    }

    // Inner classes for JSON deserialization
    private static class MockTodoData {
        public String todo;
        public String description;
        public String status;
    }

    private static class TestTodoData {
        public String id;
        public String todo;
        public String description;
        public String status;
    }
}

