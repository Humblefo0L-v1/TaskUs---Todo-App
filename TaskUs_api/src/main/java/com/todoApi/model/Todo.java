package com.todoApi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Todo Entity - Represents a todo/task item
 * This maps to the "todos" table in the database
 */
@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    /**
     * Primary Key - Uniquely identifies each todo
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title - Short summary of the task
     * We want this to be required and reasonably short
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Description - Detailed information about the task
     * This can be longer and is optional (nullable = true by default)
     * 
     * @Column with columnDefinition = "TEXT" allows longer content
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Completed - Is this task done?
     * Defaults to false (incomplete) when created
     */
    @Column(nullable = false)
    private Boolean completed = false;

    /**
     * Created timestamp - When was this todo created?
     * Automatically populated by Hibernate
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Updated timestamp - When was this todo last modified?
     * Automatically updated by Hibernate on every save
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User relationship - Who owns this todo?
     * 
     * @ManyToOne - Many todos belong to One user
     *            fetch = FetchType.LAZY - Don't load user immediately
     *            Only load when we explicitly access todo.getUser()
     *            This prevents unnecessary database queries
     * 
     * @JoinColumn - Specifies the foreign key column
     *             name = "user_id" - Column name in todos table
     *             nullable = false - Every todo must have an owner
     * 
     *             In the database, this creates:
     *             todos table will have a "user_id" column
     *             FOREIGN KEY constraint linking to users(id)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}