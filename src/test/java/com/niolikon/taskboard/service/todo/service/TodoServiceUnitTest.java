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
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static com.niolikon.taskboard.service.todo.service.testdata.TodoServiceTestData.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceUnitTest {

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
    void givenTodosExist_whenReadAll_thenReturnsTodoViewList() {
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
    @Tag("Story=TBS1")
    @Tag("Scenario=1")
    void givenTodoNotCompleted_whenOwnerRequestsMarkingComplete_thenTodoIsPatched() {
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
    void givenTodoCompleted_whenOwnerRequestsMarkingComplete_thenTodoIsNotPatched() {
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
    void givenTodoCompleted_whenOwnerRequestsAnyModification_thenTodoIsNotUpdated() {
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
    void givenPendingTodosExist_whenReadAllPending_thenReturnsTodoViewList() {
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

    @Test
    @Tag("Story=TBS8")
    @Tag("Scenario=1")
    void givenMoreTodosThanPageSize_whenClientRequestsFirstPage_thenReturnsFirstPageWithMetadata() {
        // Arrange
        Pageable firstPage = pageable_firstPageSize2_fromClient;
        List<Todo> todos = List.of(todo_instance1_fromRepository, todo_instance2_fromRepository);
        Page<Todo> todosPaged = new PageImpl<>(todos, firstPage, 5);
        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, firstPage)).thenReturn(todosPaged);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(
                todoView_mapped1_fromTodoInstance,
                todoView_mapped2_fromTodoInstance
        );

        // Act
        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, firstPage);

        // Assert
        Assertions.assertThat(result.getContent()).hasSize(2);
        assertThat(result)
                .extracting(
                        PageResponse::getElementsSize,
                        PageResponse::getElementsTotal,
                        PageResponse::getPageNumber,
                        PageResponse::getPageSize,
                        PageResponse::getPageTotal,
                        PageResponse::isFirst,
                        PageResponse::isLast,
                        PageResponse::isEmpty
                )
                .containsExactly(2, 5L, 0, 2, 3, true, false, false);

        verify(todoRepository).findByOwnerUid(VALID_OWNER_UID, firstPage);
    }

    @Test
    @Tag("Story=TBS8")
    @Tag("Scenario=2")
    void givenMultiplePagesExist_whenClientRequestsSpecificPage_thenCorrectPageReturnedWithMetadata() {
        // Arrange
        Pageable secondPage = pageable_secondPageSize2_fromClient;
        List<Todo> todos = List.of(todo_instance3_fromRepository, todo_instance4_fromRepository);
        Page<Todo> todosPaged = new PageImpl<>(todos, secondPage, 5);

        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, secondPage)).thenReturn(todosPaged);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(
                todoView_mapped3_fromTodoInstance,
                todoView_mapped4_fromTodoInstance
        );

        // Act
        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, secondPage);

        // Assert
        Assertions.assertThat(result.getContent()).hasSize(2);
        assertThat(result)
                .extracting(
                        PageResponse::getElementsSize,
                        PageResponse::getElementsTotal,
                        PageResponse::getPageNumber,
                        PageResponse::getPageSize,
                        PageResponse::getPageTotal,
                        PageResponse::isFirst,
                        PageResponse::isLast,
                        PageResponse::isEmpty
                )
                .containsExactly(2, 5L, 1, 2, 3, false, false, false);

        verify(todoRepository).findByOwnerUid(VALID_OWNER_UID, secondPage);
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
