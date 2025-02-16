package com.niolikon.taskboard.domain.todo;

import com.niolikon.taskboard.domain.todo.dto.TodoRequest;
import com.niolikon.taskboard.domain.todo.dto.TodoView;
import com.niolikon.taskboard.domain.todo.service.ITodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {
    private static final String STUB_JWT_CLAIMS_SUBJECT = "test-user-uid-very-long-and-unique";

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
        when(stubJwt.getSubject()).thenReturn(STUB_JWT_CLAIMS_SUBJECT);
        TodoRequest request = new TodoRequest("Title", "Description", Boolean.FALSE, Date.from(Instant.now()));
        TodoView createdTodo = new TodoView(1L, "Title", "Description", Boolean.FALSE, request.getDueDate());
        when(todoService.create(anyString(), any(TodoRequest.class))).thenReturn(createdTodo);
        ServletUriComponentsBuilder uriComponentsBuilder = mock(ServletUriComponentsBuilder.class);
        when(uriComponentsBuilder.path("/{id}")).thenReturn(uriComponentsBuilder);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.buildAndExpand(createdTodo.getId())).thenReturn(uriComponents);
        URI mockUri = URI.create("http://localhost/api/Todos/1");
        when(uriComponents.toUri()).thenReturn(mockUri);

        ResponseEntity<TodoView> response = todoController.create(stubJwt, request, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo(createdTodo);
        verify(todoService, times(1)).create(STUB_JWT_CLAIMS_SUBJECT, request);
    }

    @Test
    void givenValidInput_whenReadAllTodos_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(STUB_JWT_CLAIMS_SUBJECT);
        List<TodoView> todos = List.of(
                new TodoView(1L, "Task 1", "Description 1", false, Date.from(Instant.now())),
                new TodoView(2L, "Task 2", "Description 2", true, Date.from(Instant.now())));
        when(todoService.readAll(anyString())).thenReturn(todos);

        ResponseEntity<List<TodoView>> response = todoController.readAll(stubJwt);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(todos);
        verify(todoService, times(1)).readAll(STUB_JWT_CLAIMS_SUBJECT);
    }

    @Test
    void givenValidInput_whenReadTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(STUB_JWT_CLAIMS_SUBJECT);
        TodoView todo = new TodoView(1L, "Task 1", "Description", false, Date.from(Instant.now()));
        when(todoService.read(anyString(), eq(1L))).thenReturn(todo);

        ResponseEntity<TodoView> response = todoController.read(stubJwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(todo);
        verify(todoService, times(1)).read(STUB_JWT_CLAIMS_SUBJECT, 1L);
    }

    @Test
    void givenValidInput_whenUpdateTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(STUB_JWT_CLAIMS_SUBJECT);
        TodoRequest updateRequest = new TodoRequest("Title", "Updated Description", Boolean.TRUE, Date.from(Instant.now()));
        TodoView updatedTodo = new TodoView(1L, "Title", "Updated Description", Boolean.TRUE, updateRequest.getDueDate());
        when(todoService.update(anyString(), eq(1L), any(TodoRequest.class))).thenReturn(updatedTodo);

        ResponseEntity<TodoView> response = todoController.update(stubJwt, 1L, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(updatedTodo);
        verify(todoService, times(1)).update(STUB_JWT_CLAIMS_SUBJECT, 1L, updateRequest);
    }

    @Test
    void givenValidInput_whenDeleteTodo_thenOkIsReturned() {
        Jwt stubJwt = mock(Jwt.class);
        when(stubJwt.getSubject()).thenReturn(STUB_JWT_CLAIMS_SUBJECT);
        ResponseEntity<Void> response = todoController.delete(stubJwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        verify(todoService, times(1)).delete(STUB_JWT_CLAIMS_SUBJECT, 1L);
    }
}
