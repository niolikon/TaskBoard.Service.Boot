package com.niolikon.taskboard.domain.todo.service;

import com.niolikon.taskboard.domain.todo.dto.TodoPatch;
import com.niolikon.taskboard.domain.todo.dto.TodoRequest;
import com.niolikon.taskboard.domain.todo.dto.TodoView;

import java.util.List;

public interface ITodoService {
    TodoView create(String ownerUid, TodoRequest todoRequest);

    List<TodoView> readAll(String ownerUid);

    TodoView read(String ownerUid, Long id);

    TodoView update(String ownerUid, Long id, TodoRequest todoRequest);

    TodoView patch(String ownerUid, Long id, TodoPatch todoPatch);

    List<TodoView> readAllPending(String ownerUid);

    void delete(String ownerUid, Long id);
}
