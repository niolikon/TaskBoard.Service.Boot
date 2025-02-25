package com.niolikon.taskboard.domain.todo.service;

import com.niolikon.taskboard.application.exception.rest.EntityNotFoundRestException;
import com.niolikon.taskboard.domain.todo.TodoMapper;
import com.niolikon.taskboard.domain.todo.TodoRepository;
import com.niolikon.taskboard.domain.todo.dto.TodoRequest;
import com.niolikon.taskboard.domain.todo.dto.TodoView;
import com.niolikon.taskboard.domain.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    private static final String VALID_OWNER_UID = "user-123";
    private static final Long VALID_EXISTENT_TODO_ID = 1L;
    private static final Long VALID_NON_EXISTENT_TODO_ID = 111L;

    private TodoRepository todoRepository;
    private TodoMapper todoMapper;
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        todoMapper = mock(TodoMapper.class);
        todoService = new TodoService(todoRepository, todoMapper);
    }

    @Test
    void givenValidTodoRequest_whenCreate_thenReturnsTodoView() {
        TodoRequest request = TodoRequest.builder()
                .title("New Task").description("Task Description").isCompleted(false).build();
        Todo todo = Todo.builder()
                .title("New Task").description("Task Description").isCompleted(false).ownerUid(VALID_OWNER_UID).build();
        when(todoMapper.toTodo(request)).thenReturn(todo);
        Todo savedTodo = Todo.builder()
                .title("New Task").description("Task Description").isCompleted(false).ownerUid(VALID_OWNER_UID).id(VALID_EXISTENT_TODO_ID).build();
        when(todoRepository.save(todo)).thenReturn(savedTodo);
        TodoView expectedView = TodoView.builder()
                .title("New Task").description("Task Description").isCompleted(false).id(VALID_EXISTENT_TODO_ID).build();
        when(todoMapper.toTodoView(savedTodo)).thenReturn(expectedView);

        TodoView result = todoService.create(VALID_OWNER_UID, request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedView);
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void givenTodosExist_whenReadAll_thenReturnsTodoViewList() {
        List<Todo> todos = List.of(new Todo(), new Todo());
        when(todoRepository.findByOwnerUid(VALID_OWNER_UID)).thenReturn(todos);
        List<TodoView> expectedViews = List.of(
                new TodoView(1L, "Task 1", "Desc 1", false, Date.from(Instant.now())),
                new TodoView(2L, "Task 2", "Desc 2", true, Date.from(Instant.now()))
        );
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(expectedViews.get(0), expectedViews.get(1));

        List<TodoView> result = todoService.readAll(VALID_OWNER_UID);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedViews);
        verify(todoRepository, times(1)).findByOwnerUid(VALID_OWNER_UID);
    }

    @Test
    void givenExistingTodo_whenRead_thenReturnsTodoView() {
        Todo todo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID).build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(todo));
        TodoView expectedView = TodoView.builder()
                .id(VALID_EXISTENT_TODO_ID).title("Existing Task").description("Existing Desc").build();
        when(todoMapper.toTodoView(todo)).thenReturn(expectedView);

        TodoView result = todoService.read(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo((expectedView));
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }

    @Test
    void givenNonExistingTodo_whenRead_thenThrowsException() {
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundRestException.class, () -> todoService.read(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID));
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }

    @Test
    void givenValidTodoRequest_whenUpdate_thenReturnsUpdatedTodoView() {
        Todo existingTodo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID).build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        TodoRequest updateRequest = TodoRequest.builder()
                .title("Updated Task").description("Updated Desc").build();
        Todo updatedTodo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).title("Updated Task").description("Updated Desc").ownerUid(VALID_OWNER_UID).build();
        when(todoMapper.toTodo(updateRequest)).thenReturn(updatedTodo);
        when(todoRepository.save(existingTodo)).thenReturn(updatedTodo);
        TodoView expectedView = TodoView.builder()
                .id(VALID_EXISTENT_TODO_ID).title("Updated Task").description("Updated Desc").build();
        when(todoMapper.toTodoView(updatedTodo)).thenReturn(expectedView);

        TodoView result = todoService.update(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedView);
        verify(todoRepository, times(1)).save(existingTodo);
    }

    @Test
    void givenNonExistingTodo_whenUpdate_thenThrowsException() {
        TodoRequest updateRequest = TodoRequest.builder()
                .title("Updated Task").description("Updated Desc").build();
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundRestException.class, () -> todoService.update(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID, updateRequest));
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }

    @Test
    void givenExistingTodo_whenDelete_thenRemovesTodo() {
        Todo todo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID).build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(todo));

        todoService.delete(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID);

        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void givenNonExistingTodo_whenDelete_thenThrowsException() {
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundRestException.class, () -> todoService.delete(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID));
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }
}
