package dev.hieu.springboothelloworld.dto;

import dev.hieu.springboothelloworld.domain.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoCreateDTO {
    
    @NotBlank(message = "Todo title is required")
    private String todo;
    
    private String description;
    
    private Status status = Status.PENDING;
}

