package com.niolikon.taskboard.service.todo.service;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.framework.exceptions.rest.client.EntityNotFoundRestException;
import com.niolikon.taskboard.framework.exceptions.rest.client.ForbiddenRestException;
import com.niolikon.taskboard.service.todo.TodoMapper;
import com.niolikon.taskboard.service.todo.TodoRepository;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.model.Todo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Transactional
public class TodoService implements ITodoService {
    private static final String TODO_NOT_FOUND = "Could not find Todo";

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    public TodoService(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    public TodoView create(String ownerUid, TodoRequest todoRequest) {
        Todo todo = todoMapper.toTodo(todoRequest);
        todo.setOwnerUid(ownerUid);
        return todoMapper.toTodoView(todoRepository.save(todo));
    }

    @Override
    public PageResponse<TodoView> readAll(String ownerUid, Pageable pageable) {
        Page<Todo> todos = todoRepository.findByOwnerUid(ownerUid, pageable);
        return new PageResponse<>(todos.map(todoMapper::toTodoView));
    }

    @Override
    public TodoView read(String ownerUid, Long id) {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException(TodoService.TODO_NOT_FOUND));
        return todoMapper.toTodoView(todo);
    }

    @Override
    public TodoView update(String ownerUid, Long id, TodoRequest todoRequest) {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException(TodoService.TODO_NOT_FOUND));
        if (Boolean.TRUE.equals(todo.getIsCompleted()))  {
            throw new ForbiddenRestException("Cannot modify completed Todo");
        }
        todo.updateFrom(todoMapper.toTodo(todoRequest));
        return todoMapper.toTodoView(todoRepository.save(todo));
    }

    @Override
    public TodoView patch(String ownerUid, Long id, TodoPatch todoPatch)
    {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException(TodoService.TODO_NOT_FOUND));
        if (Objects.isNull(todo.getIsCompleted()) || (! todo.getIsCompleted()))  {
            todo.updateFrom(todoMapper.toTodo(todoPatch));
            todo = todoRepository.save(todo);
        }

        return todoMapper.toTodoView(todo);
    }

    @Override
    public PageResponse<TodoView> readAllPending(String ownerUid, Pageable pageable)
    {
        Page<Todo> pendingTodos = todoRepository.findByOwnerUidAndIsCompleted(ownerUid, Boolean.FALSE, pageable);
        return new PageResponse<>(pendingTodos.map(todoMapper::toTodoView));
    }

    @Override
    public PageResponse<TodoView> readAllCompleted(String ownerUid, Pageable pageable)
    {
        Page<Todo> pendingTodos = todoRepository.findByOwnerUidAndIsCompleted(ownerUid, Boolean.TRUE, pageable);
        return new PageResponse<>(pendingTodos.map(todoMapper::toTodoView));
    }

    @Override
    public void delete(String ownerUid, Long id) {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException(TodoService.TODO_NOT_FOUND));
        todoRepository.delete(todo);
    }
}
