package com.niolikon.taskboard.service.todo.service;

import com.niolikon.taskboard.framework.exceptions.rest.client.EntityNotFoundRestException;
import com.niolikon.taskboard.service.todo.TodoMapper;
import com.niolikon.taskboard.service.todo.TodoRepository;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.model.Todo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<TodoView> readAll(String ownerUid) {
        List<Todo> todos = todoRepository.findByOwnerUid(ownerUid);
        return todos.stream().map(todoMapper::toTodoView).toList();
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
            todoRepository.save(todo);
        }

        return todoMapper.toTodoView(todo);
    }

    @Override
    public List<TodoView> readAllPending(String ownerUid)
    {
        List<Todo> pendingTodos = todoRepository.findByOwnerUidAndIsCompleted(ownerUid, Boolean.FALSE);
        return pendingTodos.parallelStream().map(todoMapper::toTodoView).toList();
    }

    @Override
    public void delete(String ownerUid, Long id) {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException(TodoService.TODO_NOT_FOUND));
        todoRepository.delete(todo);
    }
}
