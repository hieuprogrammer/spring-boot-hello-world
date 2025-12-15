package dev.hieu.springboothelloworld.dto;

import dev.hieu.springboothelloworld.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for updating an existing todo (all fields are optional)")
public class TodoUpdateDTO {
    
    @Schema(description = "Title of the todo", example = "Updated todo title")
    private String todo;
    
    @Schema(description = "Description of the todo", example = "Updated description")
    private String description;
    
    @Schema(description = "Status of the todo", example = "IN_PROGRESS")
    private Status status;
}

