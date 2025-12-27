package com.todoApi;

import com.todoApi.model.Todo;
import com.todoApi.model.User;
import com.todoApi.repository.TodoRepository;
import com.todoApi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader - Initializes test data on application startup
 * This version is idempotent: it avoids inserting duplicate users/todos.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("Loading test data into database...");
        System.out.println("========================================");

        // Ensure user1 exists (create only if needed)
        String email1 = "john@example.com";
        User user1 = userRepository.findByEmail(email1).orElseGet(() -> {
            User u = new User();
            u.setUsername("john_doe");
            u.setEmail(email1);
            u.setPassword(passwordEncoder.encode("password123"));
            User saved = userRepository.save(u);
            System.out.println("✓ Created user: " + saved.getUsername() + " (id=" + saved.getId() + ")");
            return saved;
        });
        if (user1.getId() != null) {
            System.out.println("ℹ Using user: " + user1.getUsername() + " (id=" + user1.getId() + ")");
        }

        // Ensure user2 exists (create only if needed)
        String email2 = "jane@example.com";
        User user2 = userRepository.findByEmail(email2).orElseGet(() -> {
            User u = new User();
            u.setUsername("jane_smith");
            u.setEmail(email2);
            u.setPassword(passwordEncoder.encode("password123"));
            User saved = userRepository.save(u);
            System.out.println("✓ Created user: " + saved.getUsername() + " (id=" + saved.getId() + ")");
            return saved;
        });
        if (user2.getId() != null) {
            System.out.println("ℹ Using user: " + user2.getUsername() + " (id=" + user2.getId() + ")");
        }

        // Only create todos if there are none yet (prevents duplicates on restart)
        if (todoRepository.count() == 0) {
            Todo todo1 = new Todo();
            todo1.setTitle("Buy groceries");
            todo1.setDescription("Milk, eggs, bread, and coffee");
            todo1.setCompleted(false);
            todo1.setUser(user1);
            todoRepository.save(todo1);
            System.out.println("✓ Created todo: " + todo1.getTitle() + " for " + user1.getUsername());

            Todo todo2 = new Todo();
            todo2.setTitle("Finish project report");
            todo2.setDescription("Complete the Q4 analysis report");
            todo2.setCompleted(false);
            todo2.setUser(user1);
            todoRepository.save(todo2);
            System.out.println("✓ Created todo: " + todo2.getTitle() + " for " + user1.getUsername());

            Todo todo3 = new Todo();
            todo3.setTitle("Call dentist");
            todo3.setDescription("Schedule annual checkup");
            todo3.setCompleted(true);
            todo3.setUser(user1);
            todoRepository.save(todo3);
            System.out.println("✓ Created todo: " + todo3.getTitle() + " for " + user1.getUsername() + " (completed)");

            Todo todo4 = new Todo();
            todo4.setTitle("Read book");
            todo4.setDescription("Finish reading 'Clean Code'");
            todo4.setCompleted(false);
            todo4.setUser(user2);
            todoRepository.save(todo4);
            System.out.println("✓ Created todo: " + todo4.getTitle() + " for " + user2.getUsername());
        } else {
            System.out.println("ℹ Todos already exist; skipping todo creation.");
        }

        System.out.println("========================================");
        System.out.println("Test data loaded successfully!");
        System.out.println("Users created: " + userRepository.count());
        System.out.println("Todos created: " + todoRepository.count());
        System.out.println("========================================");
        System.out.println("You can now test the API:");
        System.out.println("  GET    http://localhost:8081/api/todos");
        System.out.println("  POST   http://localhost:8081/api/todos");
        System.out.println("  GET    http://localhost:8081/api/todos/1");
        System.out.println("  PATCH  http://localhost:8081/api/todos/1");
        System.out.println("  DELETE http://localhost:8081/api/todos/1");
        System.out.println("========================================");
    }
}