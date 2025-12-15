package dev.hieu.springboothelloworld.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {
    
    @Schema(description = "List of items in the current page", type = "array")
    private List<T> content;
    
    @Schema(description = "Current page number (0-indexed)")
    private int page;
    
    @Schema(description = "Page size")
    private int size;
    
    @Schema(description = "Total number of elements")
    @JsonProperty("totalElements")
    private long totalElements;
    
    @Schema(description = "Total number of pages")
    @JsonProperty("totalPages")
    private int totalPages;
    
    @Schema(description = "Whether this is the first page")
    private boolean first;
    
    @Schema(description = "Whether this is the last page")
    private boolean last;
}

