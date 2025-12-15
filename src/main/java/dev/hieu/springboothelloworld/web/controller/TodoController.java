package dev.hieu.springboothelloworld.web.controller;

import dev.hieu.springboothelloworld.configuration.FeatureFlag;
import dev.hieu.springboothelloworld.configuration.FeatureFlagService;
import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.dto.PageResponse;
import dev.hieu.springboothelloworld.dto.TodoDTO;
import dev.hieu.springboothelloworld.exception.ResourceNotFoundException;
import dev.hieu.springboothelloworld.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final FeatureFlagService featureFlagService;

    @GetMapping
    public String listTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Status status,
            Model model) {
        
        // Validate and constrain page size
        if (size < 1) {
            size = 1;
        } else if (size > 100) {
            size = 100;
        }
        
        Pageable pageable = createPageable(page, size, sort);
        PageResponse<TodoDTO> pageResponse;
        
        boolean canSearch = featureFlagService.isEnabled(FeatureFlag.TODO_SEARCH_API);

        if (canSearch && (keyword != null && !keyword.trim().isEmpty() || status != null)) {
            pageResponse = todoService.searchTodos(keyword, status, pageable);
        } else {
            pageResponse = todoService.getAllTodos(pageable);
        }
        
        // Adjust page if it's out of bounds after size change
        if (page >= pageResponse.getTotalPages() && pageResponse.getTotalPages() > 0) {
            page = pageResponse.getTotalPages() - 1;
            // Redirect to corrected page
            return "redirect:/todos?page=" + page + "&size=" + size + 
                   (sort != null ? "&sort=" + sort : "") +
                   (keyword != null ? "&keyword=" + keyword : "") +
                   (status != null ? "&status=" + status : "");
        }
        
        model.addAttribute("todos", pageResponse.getContent());
        model.addAttribute("pageResponse", pageResponse);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("pageNumbers", calculatePageNumbers(page, pageResponse.getTotalPages()));
        
        return "todos/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)) {
            model.addAttribute("errorMessage", "Todo write operations are currently disabled.");
            return "redirect:/todos";
        }
        model.addAttribute("todo", new TodoDTO());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("isEdit", false);
        return "todos/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        try {
            if (!featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)) {
                model.addAttribute("errorMessage", "Todo write operations are currently disabled.");
                return "redirect:/todos";
            }
            TodoDTO todo = todoService.getTodoById(id);
            model.addAttribute("todo", todo);
            model.addAttribute("statuses", Status.values());
            model.addAttribute("isEdit", true);
            return "todos/form";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/todos";
        }
    }

    @PostMapping
    public String createTodo(
            @RequestParam String todo,
            @RequestParam(required = false) String description,
            @RequestParam Status status,
            RedirectAttributes redirectAttributes) {
        if (!featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Todo write operations are currently disabled.");
            return "redirect:/todos";
        }

        try {
            dev.hieu.springboothelloworld.dto.TodoCreateDTO createDTO = 
                new dev.hieu.springboothelloworld.dto.TodoCreateDTO();
            createDTO.setTodo(todo);
            createDTO.setDescription(description);
            createDTO.setStatus(status);
            
            todoService.createTodo(createDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Todo created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create todo: " + e.getMessage());
        }
        
        return "redirect:/todos";
    }

    @PostMapping("/{id}")
    public String updateTodo(
            @PathVariable UUID id,
            @RequestParam String todo,
            @RequestParam(required = false) String description,
            @RequestParam Status status,
            RedirectAttributes redirectAttributes) {
        if (!featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Todo write operations are currently disabled.");
            return "redirect:/todos";
        }

        try {
            dev.hieu.springboothelloworld.dto.TodoUpdateDTO updateDTO = 
                new dev.hieu.springboothelloworld.dto.TodoUpdateDTO();
            updateDTO.setTodo(todo);
            updateDTO.setDescription(description);
            updateDTO.setStatus(status);
            
            todoService.updateTodo(id, updateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Todo updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update todo: " + e.getMessage());
        }
        
        return "redirect:/todos";
    }

    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        if (!featureFlagService.isEnabled(FeatureFlag.TODO_WRITE_API)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Todo write operations are currently disabled.");
            return "redirect:/todos";
        }

        try {
            todoService.deleteTodo(id);
            redirectAttributes.addFlashAttribute("successMessage", "Todo deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete todo: " + e.getMessage());
        }
        
        return "redirect:/todos";
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort sortObj = Sort.unsorted();
        
        if (sort != null && !sort.trim().isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0].trim();
                String direction = sortParams[1].trim().toUpperCase();
                
                if (isValidSortProperty(property)) {
                    Sort.Direction sortDirection = direction.equals("DESC") 
                            ? Sort.Direction.DESC 
                            : Sort.Direction.ASC;
                    sortObj = Sort.by(sortDirection, property);
                }
            }
        }
        
        return PageRequest.of(page, size, sortObj);
    }

    private boolean isValidSortProperty(String property) {
        return property.equals("todo") || 
               property.equals("description") || 
               property.equals("status") ||
               property.equals("id");
    }
    
    /**
     * Calculate page numbers to display in pagination.
     * Shows current page and 2 pages on each side, plus first and last pages if needed.
     */
    private List<Integer> calculatePageNumbers(int currentPage, int totalPages) {
        List<Integer> pageNumbers = new ArrayList<>();
        
        if (totalPages <= 7) {
            // If 7 or fewer pages, show all
            for (int i = 0; i < totalPages; i++) {
                pageNumbers.add(i);
            }
        } else {
            // Calculate the range of pages to show around current page
            int startPage = Math.max(0, currentPage - 2);
            int endPage = Math.min(totalPages - 1, currentPage + 2);
            
            // Always show first page if not in range
            if (startPage > 0) {
                pageNumbers.add(0);
                // Add ellipsis marker (-1) if there's a gap
                if (startPage > 1) {
                    pageNumbers.add(-1);
                }
            }
            
            // Add pages around current page
            for (int i = startPage; i <= endPage; i++) {
                pageNumbers.add(i);
            }
            
            // Always show last page if not in range
            if (endPage < totalPages - 1) {
                // Add ellipsis marker (-1) if there's a gap
                if (endPage < totalPages - 2) {
                    pageNumbers.add(-1);
                }
                pageNumbers.add(totalPages - 1);
            }
        }
        
        return pageNumbers;
    }
}

