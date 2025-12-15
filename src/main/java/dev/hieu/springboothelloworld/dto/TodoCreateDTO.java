package dev.hieu.springboothelloworld.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.hieu.springboothelloworld.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for creating a new todo")
public class TodoCreateDTO {
    
    @NotBlank(message = "Todo title is required")
    @Schema(description = "Title of the todo", example = "Complete Spring Boot project", requiredMode = Schema.RequiredMode.REQUIRED)
    private String todo;
    
    @Schema(description = "Description of the todo", example = "Finish implementing all features")
    private String description;
    
    @Schema(description = "Status of the todo", example = "PENDING", defaultValue = "PENDING")
    private Status status = Status.PENDING;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Due date and time for the todo", example = "2024-12-20T17:00:00")
    private LocalDateTime dueAt;
}

