package com.niolikon.taskboard.domain.todo;

import com.niolikon.taskboard.domain.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByOwnerUid(String ownerUid);
    Optional<Todo> findByIdAndOwnerUid(Long id, String ownerUid);
}
