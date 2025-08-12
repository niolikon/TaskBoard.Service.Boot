package com.niolikon.taskboard.service.todo.service;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.framework.exceptions.rest.client.EntityNotFoundRestException;
import com.niolikon.taskboard.service.todo.TodoMapper;
import com.niolikon.taskboard.service.todo.TodoRepository;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.model.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static com.niolikon.taskboard.service.todo.service.testdata.TodoServiceTestData.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceCoreUnitTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodoMapper todoMapper;
    @InjectMocks
    private TodoService todoService;

    @Test
    void givenValidTodoRequest_whenCreate_thenReturnsTodoView() {
        // Arrange
        TodoRequest validRequest = todoRequest_valid_fromClient;
        Todo mappedTodo = todo_mapped_fromTodoRequest;
        when(todoMapper.toTodo(validRequest)).thenReturn(mappedTodo);
        Todo savedTodo = todo_saved_fromTodoRequest;
        when(todoRepository.save(mappedTodo)).thenReturn(savedTodo);
        TodoView expectedView = todoView_expected_fromSavedTodo;
        when(todoMapper.toTodoView(savedTodo)).thenReturn(expectedView);

        // Act
        TodoView result = todoService.create(VALID_OWNER_UID, validRequest);

        // Assert
        assertThat(result).isEqualTo(expectedView);

        ArgumentCaptor<Todo> saved = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(saved.capture());
        assertThat(saved.getValue())
                .extracting(Todo::getTitle, Todo::getDescription, Todo::getOwnerUid)
                .containsExactly(
                        todoRequest_valid_fromClient.getTitle(),
                        todoRequest_valid_fromClient.getDescription(),
                        VALID_OWNER_UID
                );

        verifyNoMoreInteractions(todoRepository, todoMapper); // On mutable operations with strict contract
    }

    @Test
    void givenMultipleTodosExist_whenReadAll_thenReturnsTodoViewList() {
        // Arrange
        List<Todo> todos = List.of(todo_instance1_fromRepository, todo_instance2_fromRepository);
        Page<Todo> todosPaged = new PageImpl<>(todos, pageable_firstPageSize10_fromClient, todos.size());
        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, pageable_firstPageSize10_fromClient)).thenReturn(todosPaged);
        List<TodoView> expectedViews = List.of(todoView_instance1_fromRepository, todoView_instance2_fromRepository);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(expectedViews.get(0), expectedViews.get(1));

        // Act
        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, pageable_firstPageSize10_fromClient);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(expectedViews);
        verify(todoRepository).findByOwnerUid(VALID_OWNER_UID, pageable_firstPageSize10_fromClient);
    }

    @Test
    void givenExistingTodo_whenRead_thenReturnsTodoView() {
        // Arrange
        Todo todo = todo_existing_fromRepository;
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(todo));
        TodoView expectedView = todoView_expected_fromFoundTodo;
        when(todoMapper.toTodoView(todo)).thenReturn(expectedView);

        // Act
        TodoView result = todoService.read(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID);

        // Assert
        assertThat(result).isEqualTo(expectedView);
        verify(todoRepository).findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }

    @Test
    void givenNonExistingTodo_whenRead_thenThrowsException() {
        // Arrange
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> todoService.read(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID))
                .isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
        // no verifyNoMoreInteractions on read only operations, because refactor is more likely to occurr
    }

    @Test
    void givenValidTodoRequest_whenUpdate_thenReturnsUpdatedTodoView() {
        // Arrange
        TodoRequest validRequest = todoRequest_validUpdate_fromClient;
        Todo existingTodo = todo_existing_fromRepository;
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        when(todoMapper.toTodo(validRequest)).thenReturn(todo_mapped_fromUpdateTodoRequest);
        when(todoRepository.save(existingTodo)).thenReturn(todo_saved_fromMappedTodo);
        TodoView expectedView = todoView_mapped_fromSavedTodo;
        when(todoMapper.toTodoView(todo_saved_fromMappedTodo)).thenReturn(expectedView);

        // Act
        TodoView result = todoService.update(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, validRequest);

        // Assert
        assertThat(result).isEqualTo(expectedView);

        ArgumentCaptor<Todo> saved = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(saved.capture());
        assertThat(saved.getValue())
                .extracting(Todo::getTitle, Todo::getDescription, Todo::getOwnerUid)
                .containsExactly(
                        todoRequest_validUpdate_fromClient.getTitle(),
                        todoRequest_validUpdate_fromClient.getDescription(),
                        VALID_OWNER_UID
                );

        verifyNoMoreInteractions(todoRepository, todoMapper);
    }

    @Test
    void givenNonExistingTodo_whenUpdate_thenThrowsException() {
        // Arrange
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> todoService.update(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID, todoRequest_validUpdate_fromClient))
                .isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
        verifyNoMoreInteractions(todoRepository, todoMapper); // On error/short-circuit path
    }

    @Test
    void givenExistingTodo_whenDelete_thenRemovesTodo() {
        // Arrange
        Todo todo = todo_existing_fromRepository;
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(todo));

        // Act
        todoService.delete(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID);

        // Assert
        verify(todoRepository).delete(todo);
    }

    @Test
    void givenNonExistingTodo_whenDelete_thenThrowsException() {
        // Arrange
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> todoService.delete(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID))
                .isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
        verifyNoMoreInteractions(todoRepository, todoMapper);
    }
}
