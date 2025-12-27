package com.todoApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


/**
 * User Entity: Represents the User model in the application.
 * This maps to the "users" table in the database
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** Unique identifier for the user */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username for the User model */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** User Email for login */
    @Column(nullable = false, unique = true, length = 80)
    private String email;

    /** User Password for authentication */
    @Column(nullable = false)
    private String password;

    /** Timestamp when the user was created */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
