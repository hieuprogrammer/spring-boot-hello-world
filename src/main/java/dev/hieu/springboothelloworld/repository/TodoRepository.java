package dev.hieu.springboothelloworld.repository;

import dev.hieu.springboothelloworld.domain.Status;
import dev.hieu.springboothelloworld.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    
    List<Todo> findByStatus(Status status);
    
    Page<Todo> findByStatus(Status status, Pageable pageable);
    
    @Query("SELECT t FROM Todo t WHERE " +
           "(:keyword IS NULL OR LOWER(t.todo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR t.status = :status)")
    List<Todo> searchTodos(@Param("keyword") String keyword, @Param("status") Status status);
    
    @Query("SELECT t FROM Todo t WHERE " +
           "(:keyword IS NULL OR LOWER(t.todo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<Todo> searchTodos(@Param("keyword") String keyword, @Param("status") Status status, Pageable pageable);
}

