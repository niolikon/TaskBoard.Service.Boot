package com.niolikon.taskboard.service.todo.service;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import org.springframework.data.domain.Pageable;

public interface ITodoService {
    TodoView create(String ownerUid, TodoRequest todoRequest);

    PageResponse<TodoView> readAll(String ownerUid, Pageable pageable);

    TodoView read(String ownerUid, Long id);

    TodoView update(String ownerUid, Long id, TodoRequest todoRequest);

    TodoView patch(String ownerUid, Long id, TodoPatch todoPatch);

    PageResponse<TodoView> readAllPending(String ownerUid, Pageable pageable);

    PageResponse<TodoView> readAllCompleted(String ownerUid, Pageable pageable);

    void delete(String ownerUid, Long id);
}
