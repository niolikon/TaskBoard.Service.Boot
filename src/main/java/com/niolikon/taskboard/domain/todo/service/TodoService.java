package com.niolikon.taskboard.domain.todo.service;

import com.niolikon.taskboard.application.exception.rest.EntityNotFoundRestException;
import com.niolikon.taskboard.domain.todo.TodoMapper;
import com.niolikon.taskboard.domain.todo.TodoRepository;
import com.niolikon.taskboard.domain.todo.dto.TodoPatch;
import com.niolikon.taskboard.domain.todo.dto.TodoRequest;
import com.niolikon.taskboard.domain.todo.dto.TodoView;
import com.niolikon.taskboard.domain.todo.model.Todo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TodoService implements ITodoService {

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
                .orElseThrow(() -> new EntityNotFoundRestException("Could not find Todo"));
        return todoMapper.toTodoView(todo);
    }

    @Override
    public TodoView update(String ownerUid, Long id, TodoRequest todoRequest) {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException("Could not find Todo"));
        todo.updateFrom(todoMapper.toTodo(todoRequest));
        return todoMapper.toTodoView(todoRepository.save(todo));
    }

    @Override
    public TodoView patch(String ownerUid, Long id, TodoPatch todoPatch)
    {
        return TodoView.builder().build();
    }

    @Override
    public List<TodoView> readAllPending(String ownerUid)
    {
        return List.of(TodoView.builder().build());
    }

    @Override
    public void delete(String ownerUid, Long id) {
        Todo todo = todoRepository.findByIdAndOwnerUid(id, ownerUid)
                .orElseThrow(() -> new EntityNotFoundRestException("Could not find Todo"));
        todoRepository.delete(todo);
    }
}
