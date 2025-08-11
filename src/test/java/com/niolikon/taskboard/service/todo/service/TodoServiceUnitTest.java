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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceUnitTest {
    private static final String VALID_OWNER_UID = "user-123";
    private static final Long VALID_EXISTENT_TODO_ID = 1L;
    private static final Long VALID_NON_EXISTENT_TODO_ID = 111L;
    private static final Pageable VALID_FIRST_PAGE = PageRequest.of(0, 10);

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
        Page<Todo> todosPaged = new PageImpl<>(todos, VALID_FIRST_PAGE, todos.size());
        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, VALID_FIRST_PAGE)).thenReturn(todosPaged);
        List<TodoView> expectedViews = List.of(
                new TodoView(1L, "Task 1", "Desc 1", false, Date.from(Instant.now())),
                new TodoView(2L, "Task 2", "Desc 2", true, Date.from(Instant.now()))
        );
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(expectedViews.get(0), expectedViews.get(1));

        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, VALID_FIRST_PAGE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(expectedViews);
        verify(todoRepository, times(1)).findByOwnerUid(VALID_OWNER_UID, VALID_FIRST_PAGE);
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

        assertThatThrownBy(() -> todoService.read(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID)).isInstanceOf(EntityNotFoundRestException.class);
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

        assertThatThrownBy(() -> todoService.update(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID, updateRequest)).isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }

    @Test
    @Tag("Story=TBS1")
    @Tag("Scenario=1")
    void givenTodoNotCompleted_whenOwnerRequestsMarkingComplete_thenTodoIsPatched() {
        Todo existingTodo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
                .title("Title").description("Description").isCompleted(Boolean.FALSE).dueDate(Date.from(Instant.now()))
                .build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        TodoPatch todoPatch = TodoPatch.builder()
                .isCompleted(Boolean.TRUE)
                .build();
        Todo todoWithPatchFields = Todo.builder()
                .isCompleted(Boolean.TRUE)
                .build();
        when(todoMapper.toTodo(todoPatch)).thenReturn(todoWithPatchFields);
        Todo patchedTodo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
                .title(existingTodo.getTitle()).description(existingTodo.getDescription())
                .isCompleted(todoWithPatchFields.getIsCompleted())
                .dueDate(existingTodo.getDueDate())
                .build();
        when(todoRepository.save(existingTodo)).thenReturn(patchedTodo);
        TodoView expectedView = TodoView.builder()
                .id(VALID_EXISTENT_TODO_ID)
                .title(patchedTodo.getTitle()).description(patchedTodo.getDescription()).isCompleted(patchedTodo.getIsCompleted())
                .dueDate(patchedTodo.getDueDate())
                .build();
        when(todoMapper.toTodoView(patchedTodo)).thenReturn(expectedView);

        TodoView result = todoService.patch(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, todoPatch);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedView);
        verify(todoRepository, times(1)).save(existingTodo);
    }

    @Test
    @Tag("Story=TBS1")
    @Tag("Scenario=2")
    void givenTodoCompleted_whenOwnerRequestsMarkingComplete_thenTodoIsNotPatched() {
        Todo existingTodo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
                .title("Title").description("Description").isCompleted(Boolean.TRUE).dueDate(Date.from(Instant.now()))
                .build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        TodoPatch todoPatch = TodoPatch.builder()
                .description("Patched Description").isCompleted(Boolean.TRUE)
                .build();
        TodoView expectedView = TodoView.builder()
                .id(VALID_EXISTENT_TODO_ID)
                .title(existingTodo.getTitle()).description(existingTodo.getDescription()).isCompleted(existingTodo.getIsCompleted())
                .dueDate(existingTodo.getDueDate())
                .build();
        when(todoMapper.toTodoView(existingTodo)).thenReturn(expectedView);

        TodoView result = todoService.patch(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, todoPatch);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedView);
        verify(todoRepository, never()).save(existingTodo);
    }

    @Test
    @Tag("Story=TBS5")
    @Tag("Scenario=1")
    void givenTodoCompleted_whenOwnerRequestsAnyModification_thenTodoIsNotUpdated() {
        Todo existingTodo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
                .title("Title").description("Description").isCompleted(Boolean.TRUE).dueDate(Date.from(Instant.now()))
                .build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(existingTodo));
        TodoRequest todoModificationRequest = TodoRequest.builder()
                .title("Updated Title").isCompleted(Boolean.TRUE)
                .build();

        assertThatThrownBy( () -> todoService.update(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID, todoModificationRequest))
                .withFailMessage("Cannot modify completed Todo")
                .isInstanceOf(ForbiddenRestException.class);

        verify(todoRepository, never()).save(existingTodo);
    }

    @Test
    @Tag("Story=TBS1")
    void givenNonExistingTodo_whenPatch_thenThrowsException() {
        TodoPatch todoPatch = TodoPatch.builder()
                .isCompleted(Boolean.TRUE).build();
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.patch(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID, todoPatch)).isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }

    @Test
    @Tag("Story=TBS1")
    void givenPendingTodosExist_whenReadAllPending_thenReturnsTodoViewList() {
        List<Todo> pendingTodos = List.of(new Todo(), new Todo());
        Page<Todo> pendingTodosPaged = new PageImpl<>(pendingTodos, VALID_FIRST_PAGE, pendingTodos.size());
        when(todoRepository.findByOwnerUidAndIsCompleted(VALID_OWNER_UID, Boolean.FALSE, VALID_FIRST_PAGE)).thenReturn(pendingTodosPaged);
        List<TodoView> expectedViews = List.of(
                new TodoView(1L, "Task 1", "Desc 1", false, Date.from(Instant.now())),
                new TodoView(2L, "Task 2", "Desc 2", false, Date.from(Instant.now()))
        );
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(expectedViews.get(0), expectedViews.get(1));

        PageResponse<TodoView> result = todoService.readAllPending(VALID_OWNER_UID, VALID_FIRST_PAGE);

        assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).containsExactlyInAnyOrderElementsOf(expectedViews);
        verify(todoRepository, times(1)).findByOwnerUidAndIsCompleted(VALID_OWNER_UID, Boolean.FALSE, VALID_FIRST_PAGE);
    }

    @Test
    @Tag("Story=TBS8")
    @Tag("Scenario=1")
    void givenMoreTodosThanPageSize_whenClientRequestsFirstPage_thenReturnsFirstPageWithMetadata() {
        Pageable firstPage = PageRequest.of(0, 2);
        List<Todo> todos = List.of(new Todo(), new Todo());
        Page<Todo> todosPaged = new PageImpl<>(todos, firstPage, 5);
        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, firstPage)).thenReturn(todosPaged);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(
                new TodoView(1L, "Task 1", "Desc 1", false, Date.from(Instant.now())),
                new TodoView(2L, "Task 2", "Desc 2", false, Date.from(Instant.now()))
        );

        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, firstPage);

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
        Pageable secondPage = PageRequest.of(1, 2);
        List<Todo> todos = List.of(new Todo(), new Todo());
        Page<Todo> todosPaged = new PageImpl<>(todos, secondPage, 5);

        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, secondPage)).thenReturn(todosPaged);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(
                new TodoView(3L, "Task 3", "Desc 3", false, Date.from(Instant.now())),
                new TodoView(4L, "Task 4", "Desc 4", false, Date.from(Instant.now()))
        );

        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, secondPage);

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
        Todo todo = Todo.builder()
                .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID).build();
        when(todoRepository.findByIdAndOwnerUid(VALID_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.of(todo));

        todoService.delete(VALID_OWNER_UID, VALID_EXISTENT_TODO_ID);

        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void givenNonExistingTodo_whenDelete_thenThrowsException() {
        when(todoRepository.findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.delete(VALID_OWNER_UID, VALID_NON_EXISTENT_TODO_ID)).isInstanceOf(EntityNotFoundRestException.class);
        verify(todoRepository, times(1)).findByIdAndOwnerUid(VALID_NON_EXISTENT_TODO_ID, VALID_OWNER_UID);
    }
}
