package dev.hieu.springboothelloworld.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.hieu.springboothelloworld.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Todo data transfer object")
public class TodoDTO {
    
    @Schema(description = "Unique identifier of the todo", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @NotBlank(message = "Todo title is required")
    @Schema(description = "Title of the todo", example = "Complete Spring Boot project", requiredMode = Schema.RequiredMode.REQUIRED)
    private String todo;
    
    @Schema(description = "Description of the todo", example = "Finish implementing all features")
    private String description;
    
    @NotNull(message = "Status is required")
    @Schema(description = "Status of the todo", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    private Status status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the todo was created", example = "2024-12-16T10:30:00")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the todo was last updated", example = "2024-12-16T14:45:00")
    private LocalDateTime lastUpdatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Due date and time for the todo", example = "2024-12-20T17:00:00")
    private LocalDateTime dueAt;
}

