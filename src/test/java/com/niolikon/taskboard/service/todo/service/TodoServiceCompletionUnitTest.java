package com.niolikon.taskboard.service.todo.service;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.framework.exceptions.rest.client.EntityNotFoundRestException;
import com.niolikon.taskboard.framework.exceptions.rest.client.ForbiddenRestException;
import com.niolikon.taskboard.service.todo.TodoMapper;
import com.niolikon.taskboard.service.todo.TodoRepository;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.model.Todo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static com.niolikon.taskboard.service.todo.service.testdata.TodoServiceTestData.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceCompletionUnitTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodoMapper todoMapper;
    @InjectMocks
    private TodoService todoService;

    @Test
    @Tag("Story=TBS1")
    @Tag("Scenario=1")
    void givenPendingTodo_whenOwnerRequestsMarkingComplete_thenTodoIsPatched() {
        // Arrange
        TodoPatch todoPatch = todoPatch_completed_fromClient;
        when(todoMapper.toTodo(todoPatch)).thenReturn(todo_mapped_fromTodoPatch);
        Todo existingTodo = todo_existingNonCompleted_fromRepository;
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        Todo patchedTodo = todo_patchedAndSaved_fromRepository;
        when(todoRepository.save(existingTodo)).thenReturn(patchedTodo);
        TodoView expectedView = todoView_mapped_fromPatchedTodo;
        when(todoMapper.toTodoView(patchedTodo)).thenReturn(expectedView);

        // Act
        TodoView result = todoService.patch(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, todoPatch);

        // Assert
        assertThat(result).isEqualTo(expectedView);

        ArgumentCaptor<Todo> saved = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(saved.capture());
        assertThat(saved.getValue())
                .extracting(Todo::getTitle, Todo::getDescription, Todo::getIsCompleted)
                .containsExactly(
                        todo_existingNonCompleted_fromRepository.getTitle(),
                        todo_existingNonCompleted_fromRepository.getDescription(),
                        Boolean.TRUE
                );

        verifyNoMoreInteractions(todoRepository, todoMapper);
    }

    @Test
    @Tag("Story=TBS1")
    @Tag("Scenario=2")
    void givenCompletedTodo_whenOwnerRequestsMarkingComplete_thenTodoIsNotPatched() {
        // Arrange
        Todo existingTodo = todo_existingCompleted_fromRepository;
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        TodoView expectedView = todoView_mapped_fromExistingTodo;
        when(todoMapper.toTodoView(existingTodo)).thenReturn(expectedView);

        // Act
        TodoView result = todoService.patch(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, todoPatch_completed_fromClient);

        // Assert
        assertThat(result).isEqualTo(expectedView);
        verify(todoRepository, never()).save(existingTodo);
    }

    @Test
    @Tag("Story=TBS5")
    @Tag("Scenario=1")
    void givenCompletedTodo_whenOwnerRequestsAnyModification_thenTodoIsNotUpdated() {
        // Arrange
        Todo existingTodo = todo_existingCompleted_fromRepository;
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));

        // Act & Assert
        assertThatThrownBy( () -> todoService.update(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, todoRequest_modifiedTitleAndCompletion_fromClient))
                .isInstanceOf(ForbiddenRestException.class)
                .hasMessageContaining("Cannot modify completed Todo");
        verify(todoRepository, never()).save(existingTodo);
        verifyNoMoreInteractions(todoRepository, todoMapper);
    }

    @Test
    @Tag("Story=TBS1")
    void givenNonExistingTodo_whenPatch_thenThrowsException() {
        // Arrange
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> todoService.patch(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID, todoPatch_completed_fromClient))
                .isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
        verifyNoMoreInteractions(todoRepository, todoMapper);
    }

    @Test
    @Tag("Story=TBS1")
    void givenMultiplePendingTodosExist_whenReadAllPending_thenReturnsTodoViewList() {
        // Arrange
        List<Todo> pendingTodos = List.of(todo_pending1_fromRepository, todo_pending2_fromRepository);
        Page<Todo> pendingTodosPaged = new PageImpl<>(pendingTodos, pageable_firstPageSize10_fromClient, pendingTodos.size());
        when(todoRepository.findByOwnerUidAndIsCompleted(VALID_OWNER_UID, Boolean.FALSE, pageable_firstPageSize10_fromClient))
                .thenReturn(pendingTodosPaged);
        List<TodoView> expectedViews = List.of(todoView_mapped1_fromPendingTodo, todoView_mapped2_fromPendingTodo);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(todoView_mapped1_fromPendingTodo, todoView_mapped2_fromPendingTodo);

        // Act
        PageResponse<TodoView> result = todoService.readAllPending(VALID_OWNER_UID, pageable_firstPageSize10_fromClient);

        // Assert
        assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).containsExactly(expectedViews.toArray(new TodoView[0]));
        verify(todoRepository)
                .findByOwnerUidAndIsCompleted(VALID_OWNER_UID, Boolean.FALSE, pageable_firstPageSize10_fromClient);
    }
}
