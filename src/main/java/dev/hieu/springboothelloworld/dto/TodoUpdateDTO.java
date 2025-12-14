package dev.hieu.springboothelloworld.dto;

import dev.hieu.springboothelloworld.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoUpdateDTO {
    
    private String todo;
    
    private String description;
    
    private Status status;
}

