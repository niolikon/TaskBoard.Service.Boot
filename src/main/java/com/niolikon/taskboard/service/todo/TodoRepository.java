package com.niolikon.taskboard.service.todo;

import com.niolikon.taskboard.service.todo.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByOwnerUid(String ownerUid, Pageable pageable);
    Page<Todo> findByOwnerUidAndIsCompleted(String ownerUid, Boolean isCompleted, Pageable pageable);
    Optional<Todo> findByIdAndOwnerUid(Long id, String ownerUid);
}
