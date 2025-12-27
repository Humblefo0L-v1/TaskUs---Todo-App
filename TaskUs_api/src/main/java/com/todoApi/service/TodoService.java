package com.todoApi.service;

import com.todoApi.model.Todo; 
import com.todoApi.model.User;
import com.todoApi.repository.TodoRepository;
import com.todoApi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    
    /** Create a new Todo for a specific user */
    @Transactional
    public Todo createTodo(Todo todoData, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

           todoData.setUser(user);
           
           if(todoData.getCompleted() == null){
               todoData.setCompleted(false); // Default to false if not provided
           }

        return todoRepository.save(todoData);
    }

    /** Get a specific Todo by ID */
    public Todo getTodoById(Long todoId, Long userId){
        return todoRepository.findByIdAndUser_Id(todoId, userId)
                .orElseThrow(() -> new RuntimeException("Todo not found with ID: " + todoId + " for User ID: " + userId));
    }

    /** Get all todo items from a user */
    public List<Todo> getAllTodosForUser(Long userId){
        return todoRepository.findByUser_Id(userId);
    }

    /** Get all completed todo items for the given user */
    public List<Todo> getCompletedTodosForUser(Long userId){
        return todoRepository.findByUser_IdAndCompleted(userId, true);
    }

    /** Get all incomplete todo items for the given user */
    public List<Todo> getIncompleteTodosForUser(Long userId){
        return todoRepository.findByUser_IdAndCompleted(userId, false);
    }

    /** Update an existing Todo item for a specific user */
    @Transactional
    public Todo updateTodo(Long todoId, Long userId, Todo updatedTodoData){
        Todo existingTodo = getTodoById(todoId, userId);

        if(updatedTodoData.getTitle() != null){
            existingTodo.setTitle(updatedTodoData.getTitle());
        }
        if(updatedTodoData.getDescription() != null){
            existingTodo.setDescription(updatedTodoData.getDescription());
        }
        if(updatedTodoData.getCompleted() != null){
            existingTodo.setCompleted(updatedTodoData.getCompleted());
        }
        
        return todoRepository.save(existingTodo);
    }

    /**Toggle todo item completion status */
    @Transactional
    public Todo toggleTodoCompletion(Long todoId, Long userId){
        Todo existingTodo = getTodoById(todoId, userId);
        existingTodo.setCompleted(!existingTodo.getCompleted());
        return todoRepository.save(existingTodo);
    }

    /** Delete a specific Todo by ID */
    @Transactional
    public void deleteTodo(Long todoId, Long userId){
        Todo existingTodo = getTodoById(todoId, userId);
        todoRepository.delete(existingTodo);
    }

    /** Delete all todo items for given user */
    @Transactional
    public void deleteAllTodosForUser(Long userId){
        todoRepository.deleteByUser_Id(userId);
    }

    /** Count total todos for a specific user */
    public Long countTodosForUser(Long userId){
        return todoRepository.countByUser_Id(userId);
    }
}
