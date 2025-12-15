package dev.hieu.springboothelloworld.dto;

import dev.hieu.springboothelloworld.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

