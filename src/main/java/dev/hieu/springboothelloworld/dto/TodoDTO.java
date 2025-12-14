package dev.hieu.springboothelloworld.dto;

import dev.hieu.springboothelloworld.domain.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {
    
    private UUID id;
    
    @NotBlank(message = "Todo title is required")
    private String todo;
    
    private String description;
    
    @NotNull(message = "Status is required")
    private Status status;
}

