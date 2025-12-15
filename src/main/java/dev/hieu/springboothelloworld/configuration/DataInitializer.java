package dev.hieu.springboothelloworld.configuration;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.domain.Todo;
import dev.hieu.springboothelloworld.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository todoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (todoRepository.count() == 0) {
            log.info("Initializing mock data from CSV...");
            
            List<Todo> mockTodos = loadTodosFromCsv();
            
            if (mockTodos.isEmpty()) {
                log.warn("CSV file not found or empty. Generating sample data...");
                mockTodos = generateSampleData();
            }
            
            // Save in batches for better performance
            int batchSize = 1000;
            for (int i = 0; i < mockTodos.size(); i += batchSize) {
                int end = Math.min(i + batchSize, mockTodos.size());
                List<Todo> batch = new ArrayList<>(mockTodos.subList(i, end));
                todoRepository.saveAll(batch);
                log.info("Saved batch {}/{}", (i / batchSize) + 1, (mockTodos.size() + batchSize - 1) / batchSize);
            }
            
            log.info("Initialized {} mock todos", mockTodos.size());
        } else {
            log.info("Database already contains data. Skipping initialization.");
        }
    }
    
    private List<Todo> loadTodosFromCsv() {
        List<Todo> todos = new ArrayList<>();
        
        try {
            ClassPathResource resource = new ClassPathResource("data/todos.csv");
            if (!resource.exists()) {
                log.warn("CSV file not found at data/todos.csv");
                return todos;
            }
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                boolean isFirstLine = true;
                int lineNumber = 0;
                
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    
                    // Skip header line
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Parse CSV line (handling quoted fields)
                    String[] parts = parseCsvLine(line);
                    
                    if (parts.length >= 2) {
                        String todoTitle = parts[0].trim();
                        String description = parts.length > 1 ? parts[1].trim() : "";
                        Status status = parseStatus(parts.length > 2 ? parts[2].trim() : "PENDING");
                        
                        if (!todoTitle.isEmpty()) {
                            todos.add(createTodo(todoTitle, description, status));
                        }
                    }
                    
                    // Limit to 10000 records
                    if (todos.size() >= 10000) {
                        break;
                    }
                }
                
                log.info("Loaded {} todos from CSV (processed {} lines)", todos.size(), lineNumber);
            }
        } catch (Exception e) {
            log.error("Error loading CSV file: {}", e.getMessage(), e);
        }
        
        return todos;
    }
    
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentField.append('"');
                    i++;
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add last field
        fields.add(currentField.toString());
        
        return fields.toArray(new String[0]);
    }
    
    private Status parseStatus(String statusStr) {
        try {
            return Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Status.PENDING;
        }
    }
    
    private List<Todo> generateSampleData() {
        // Fallback: generate sample data if CSV is not available
        Random random = new Random();
        Status[] statuses = Status.values();
        List<Todo> todos = new ArrayList<>();
        
        String[] todoTemplates = {
            "Complete task", "Review document", "Write code", "Fix bug", "Update feature",
            "Test application", "Deploy service", "Refactor code", "Optimize performance",
            "Create documentation", "Attend meeting", "Plan sprint", "Code review",
            "Database migration", "API integration", "UI improvement", "Security audit"
        };
        
        for (int i = 1; i <= 10000; i++) {
            String title = todoTemplates[random.nextInt(todoTemplates.length)] + " #" + i;
            String description = "Description for " + title;
            Status status = statuses[random.nextInt(statuses.length)];
            todos.add(createTodo(title, description, status));
        }
        
        return todos;
    }
    
    private Todo createTodo(String todo, String description, Status status) {
        Todo entity = new Todo();
        entity.setTodo(todo);
        entity.setDescription(description);
        entity.setStatus(status);
        return entity;
    }
}

