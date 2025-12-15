package dev.hieu.springboothelloworld.dto;

import dev.hieu.springboothelloworld.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

