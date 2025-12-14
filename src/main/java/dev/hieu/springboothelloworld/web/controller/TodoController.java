package dev.hieu.springboothelloworld.web.controller;

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

import java.util.UUID;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public String listTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Status status,
            Model model) {
        
        Pageable pageable = createPageable(page, size, sort);
        PageResponse<TodoDTO> pageResponse;
        
        if (keyword != null && !keyword.trim().isEmpty() || status != null) {
            pageResponse = todoService.searchTodos(keyword, status, pageable);
        } else {
            pageResponse = todoService.getAllTodos(pageable);
        }
        
        model.addAttribute("todos", pageResponse.getContent());
        model.addAttribute("pageResponse", pageResponse);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", Status.values());
        
        return "todos/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("todo", new TodoDTO());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("isEdit", false);
        return "todos/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        try {
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
}

