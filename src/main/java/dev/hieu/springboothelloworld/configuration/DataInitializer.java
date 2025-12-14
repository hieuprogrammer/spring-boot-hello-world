package dev.hieu.springboothelloworld.configuration;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository todoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (todoRepository.count() == 0) {
            log.info("Initializing mock data...");
            
            List<Todo> mockTodos = List.of(
                createTodo("Complete Spring Boot project", "Finish implementing all features for the Spring Boot hello world application", Status.IN_PROGRESS),
                createTodo("Review code changes", "Go through the pull request and provide feedback", Status.PENDING),
                createTodo("Write unit tests", "Add comprehensive unit tests for all service methods", Status.PENDING),
                createTodo("Update documentation", "Update README with new features and setup instructions", Status.PENDING),
                createTodo("Deploy to production", "Deploy the application to production environment", Status.PENDING),
                createTodo("Fix bug in authentication", "Resolve the authentication issue reported by QA team", Status.IN_PROGRESS),
                createTodo("Attend team meeting", "Join the weekly team standup meeting", Status.COMPLETED),
                createTodo("Refactor database queries", "Optimize slow database queries for better performance", Status.CANCELLED)
            );
            
            todoRepository.saveAll(mockTodos);
            log.info("Initialized {} mock todos", mockTodos.size());
        } else {
            log.info("Database already contains data. Skipping initialization.");
        }
    }
    
    private Todo createTodo(String todo, String description, Status status) {
        Todo entity = new Todo();
        entity.setTodo(todo);
        entity.setDescription(description);
        entity.setStatus(status);
        return entity;
    }
}

