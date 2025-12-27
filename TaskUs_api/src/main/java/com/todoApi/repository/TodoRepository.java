package com.todoApi.repository;

import com.todoApi.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Todo repository: Handles database operations for Todo entity */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    /** Find todos by user ID */
    List<Todo> findByUser_Id(Long userId);

    /** Find todo by ID and user ID */
    Optional<Todo> findByIdAndUser_Id(Long id, Long userId);

    /** Find all the completed Todos for a specific user */
    List<Todo> findByUser_IdAndCompleted(Long userId, Boolean completed);

    /** Todo count for a specific user */
    Long countByUser_Id(Long userId);

    /** Delete All Todos belonging to a specific user(On user account deletion) */
    void deleteByUser_Id(Long userId);

    /** Check if todo exists for a specific user */
    Boolean existsByIdAndUser_Id(Long id, Long userId);
}