package com.todoApi.repository;

import com.todoApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** User repository: Handles database for User entity */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    /** Find user by username */
    Optional<User> findByUsername(String username);

    /** Find user by email */
    Optional<User> findByEmail(String email);

    /** Check if user with username exists */
    Boolean existsByUsername(String username);

    /** Check if user with email exists */
    Boolean existsByEmail(String email);
}
