package com.todoApi.controller;

import com.todoApi.model.Todo;
import com.todoApi.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todoData) {
        Long userId = getAuthenticatedUserId();

        Todo createdTodo = todoService.createTodo(todoData, userId);

        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@RequestParam(required = false) Boolean completed) {
        Long userId = getAuthenticatedUserId();

        List<Todo> todos;
        if (completed != null) {
            if (completed) {
                todos = todoService.getCompletedTodosForUser(userId);
            } else {
                todos = todoService.getIncompleteTodosForUser(userId);
            }
        } else {
            todos = todoService.getAllTodosForUser(userId);
        }

        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();

        Todo todo = todoService.getTodoById(id, userId);

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody Todo updatedTodoData) {
        Long userId = getAuthenticatedUserId();

        Todo updatedTodo = todoService.updateTodo(id, userId, updatedTodoData);

        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> partiallyUpdateTodo(@PathVariable Long id, @RequestBody Todo updatedTodoData) {
        Long userId = getAuthenticatedUserId();

        Todo updatedTodo = todoService.updateTodo(id, userId, updatedTodoData);

        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();

        todoService.deleteTodo(id, userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Todo> toggleTodoCompletion(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();

        Todo updatedTodo = todoService.toggleTodoCompletion(id, userId);

        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTodoCount() {
        Long userId = getAuthenticatedUserId();

        Long todoCount = todoService.countTodosForUser(userId);

        return new ResponseEntity<>(todoCount, HttpStatus.OK);
    }

    /**
     * Helper method to extract authenticated user ID from Security Context
     * 
     * This is set by JwtAuthenticationFilter after validating the JWT token
     * 
     * @return The authenticated user's ID
     * @throws RuntimeException if no authentication is present
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        // The principal is the userId we set in JwtAuthenticationFilter
        return (Long) authentication.getPrincipal();
    }
}
