package com.niolikon.taskboard.service.todo.controller;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.service.ITodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.List;

import static com.niolikon.taskboard.service.todo.controller.testdata.TodoControllerTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class TodoControllerUnitTest {

    private ITodoService todoService;
    private TodoController todoController;

    @BeforeEach
    void setUp() {
        todoService = mock(ITodoService.class);
        todoController = new TodoController(todoService);
    }

    @Test
    void givenValidInput_whenCreateTodo_thenCreatedIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        TodoRequest request = todoRequest_valid_fromClient;
        TodoView createdTodo = todoView_expected_fromTodoRequest;
        when(todoService.create(anyString(), any(TodoRequest.class))).thenReturn(createdTodo);
        ServletUriComponentsBuilder uriComponentsBuilder = mock(ServletUriComponentsBuilder.class);
        when(uriComponentsBuilder.path(TodoApiPaths.MAPPING_PATH_TODO_BY_ID)).thenReturn(uriComponentsBuilder);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.buildAndExpand(createdTodo.getId())).thenReturn(uriComponents);
        URI mockUri = new UriTemplate(TodoApiPaths.API_PATH_TODO_BY_ID).expand(createdTodo.getId());
        when(uriComponents.toUri()).thenReturn(mockUri);

        ResponseEntity<TodoView> response = todoController.create(stubJwt, request, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo(createdTodo);
        verify(todoService, times(1)).create(JWT_SUBJECT_VALID_USER_ID, request);
    }

    @Test
    void givenValidInput_whenReadAllTodos_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        List<TodoView> todos = List.of(todoView_instance1_fromRepository, todoView_instance2_fromRepository);
        Page<TodoView> todosPage = new PageImpl<>(todos, pageable_firstPageSize10_fromClient, todos.size());
        PageResponse<TodoView> todosPageResponse = new PageResponse<>(todosPage);
        when(todoService.readAll(anyString(), eq(pageable_firstPageSize10_fromClient))).thenReturn(todosPageResponse);

        ResponseEntity<PageResponse<TodoView>> response = todoController.readAll(stubJwt, pageable_firstPageSize10_fromClient);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(todosPageResponse);
        verify(todoService, times(1)).readAll(JWT_SUBJECT_VALID_USER_ID, pageable_firstPageSize10_fromClient);
    }

    @Test
    void givenValidInput_whenReadTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        TodoView todo = todoView_instance1_fromRepository;
        Long todoId = todoView_instance1_fromRepository.getId();
        when(todoService.read(anyString(), eq(todoId))).thenReturn(todo);

        ResponseEntity<TodoView> response = todoController.read(stubJwt, todoId);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(todo);
        verify(todoService, times(1)).read(JWT_SUBJECT_VALID_USER_ID, todoId);
    }

    @Test
    void givenValidInput_whenUpdateTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        Long todoId = 1L;
        TodoRequest updateRequest = todoRequest_valid_fromClient;
        TodoView updatedTodo = todoView_expected_fromTodoRequest;
        when(todoService.update(anyString(), eq(todoId), any(TodoRequest.class))).thenReturn(updatedTodo);

        ResponseEntity<TodoView> response = todoController.update(stubJwt, todoId, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(updatedTodo);
        verify(todoService, times(1)).update(JWT_SUBJECT_VALID_USER_ID, todoId, updateRequest);
    }

    @Test
    @Tag("Story=TBS1")
    void givenValidInput_whenPatchTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        Long todoId = 1L;
        TodoPatch patchRequest = todoPatch_done_fromClient;
        TodoView updatedTodo = todoView_expected_fromTodoPatchDone;
        when(todoService.patch(anyString(), eq(todoId), any(TodoPatch.class))).thenReturn(updatedTodo);

        ResponseEntity<TodoView> response = todoController.patch(stubJwt, todoId, patchRequest);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(updatedTodo);
        verify(todoService, times(1)).patch(JWT_SUBJECT_VALID_USER_ID, todoId, patchRequest);
    }

    @Test
    @Tag("Story=TBS1")
    void givenValidInput_whenReadAllPendingTodos_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        List<TodoView> pendingTodos = List.of(todoView_pending1_fromRepository, todoView_pending2_fromRepository);
        Page<TodoView> pendingTodosPage = new PageImpl<>(pendingTodos, pageable_firstPageSize10_fromClient, pendingTodos.size());
        PageResponse<TodoView> pendingTodosPageResponse = new PageResponse<>(pendingTodosPage);
        when(todoService.readAllPending(anyString(), eq(pageable_firstPageSize10_fromClient))).thenReturn(pendingTodosPageResponse);

        ResponseEntity<PageResponse<TodoView>> response = todoController.readAllPending(stubJwt, pageable_firstPageSize10_fromClient);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(pendingTodosPageResponse);
        verify(todoService, times(1)).readAllPending(JWT_SUBJECT_VALID_USER_ID, pageable_firstPageSize10_fromClient);
    }

    @Test
    void givenValidInput_whenDeleteTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(JWT_SUBJECT_VALID_USER_ID);
        Long todoId = 1L;
        ResponseEntity<Void> response = todoController.delete(stubJwt, todoId);

        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        verify(todoService, times(1)).delete(JWT_SUBJECT_VALID_USER_ID, todoId);
    }
}
