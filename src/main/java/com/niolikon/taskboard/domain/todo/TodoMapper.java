package com.niolikon.taskboard.domain.todo;

import com.niolikon.taskboard.domain.todo.dto.TodoPatch;
import com.niolikon.taskboard.domain.todo.dto.TodoRequest;
import com.niolikon.taskboard.domain.todo.dto.TodoView;
import com.niolikon.taskboard.domain.todo.model.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TodoMapper {
    TodoMapper INSTANCE = Mappers.getMapper(TodoMapper.class);

    TodoView toTodoView(Todo todo);

    Todo toTodo(TodoRequest todoRequest);

    Todo toTodo(TodoPatch todoPatch);
}
