package com.niolikon.taskboard.service.todo.controller;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.service.ITodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.niolikon.taskboard.service.todo.controller.TodoApiPaths.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(MAPPING_PATH_TODO_BASE)
public class  TodoController {

    private final ITodoService todoService;

    public TodoController(ITodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoView> create(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody TodoRequest todoRequest,
                                           ServletUriComponentsBuilder uriComponentsBuilder) {
        String ownerUid = jwt.getSubject();
        TodoView createdTodo = todoService.create(ownerUid, todoRequest);
        URI location = uriComponentsBuilder
                .path(MAPPING_PATH_TODO_BY_ID)
                .buildAndExpand(createdTodo.getId())
                .toUri();
        return created(location).body(createdTodo);
    }

    @GetMapping
    public ResponseEntity<PageResponse<TodoView>> readAll(@AuthenticationPrincipal Jwt jwt,
                                                          @PageableDefault Pageable pageable) {
        String ownerUid = jwt.getSubject();
        PageResponse<TodoView> userTodos = todoService.readAll(ownerUid, pageable);
        return ok().body(userTodos);
    }

    @GetMapping(MAPPING_PATH_TODO_BY_ID)
    public ResponseEntity<TodoView> read(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable(PATH_VARIABLE_TODO_ID) Long id) {
        String ownerUid = jwt.getSubject();
        TodoView userTodo = todoService.read(ownerUid, id);
        return ok().body(userTodo);
    }

    @PutMapping(MAPPING_PATH_TODO_BY_ID)
    public ResponseEntity<TodoView> update(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable(PATH_VARIABLE_TODO_ID) Long id,
                                           @Valid @RequestBody TodoRequest todoRequest) {
        String ownerUid = jwt.getSubject();
        TodoView userTodo = todoService.update(ownerUid, id, todoRequest);
        return ok().body(userTodo);
    }

    @PatchMapping(MAPPING_PATH_TODO_BY_ID)
    public ResponseEntity<TodoView> patch(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable(PATH_VARIABLE_TODO_ID) Long id,
                                          @Valid @RequestBody TodoPatch todoPatch) {
        String ownerUid = jwt.getSubject();
        TodoView userTodo = todoService.patch(ownerUid, id, todoPatch);
        return ok().body(userTodo);
    }

    @GetMapping(MAPPING_PATH_TODO_PENDING)
    public ResponseEntity<PageResponse<TodoView>> readAllPending(@AuthenticationPrincipal Jwt jwt,
                                                                 @PageableDefault Pageable pageable) {
        String ownerUid = jwt.getSubject();
        PageResponse<TodoView> pendingTodos = todoService.readAllPending(ownerUid, pageable);
        return ok().body(pendingTodos);
    }

    @GetMapping(MAPPING_PATH_TODO_COMPLETED)
    public ResponseEntity<PageResponse<TodoView>> readAllCompleted(@AuthenticationPrincipal Jwt jwt,
                                                                   @PageableDefault Pageable pageable) {
        String ownerUid = jwt.getSubject();
        PageResponse<TodoView> pendingTodos = todoService.readAllCompleted(ownerUid, pageable);
        return ok().body(pendingTodos);
    }

    @DeleteMapping(MAPPING_PATH_TODO_BY_ID)
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable(PATH_VARIABLE_TODO_ID) Long id) {
        String ownerUid = jwt.getSubject();
        todoService.delete(ownerUid, id);
        return noContent().build();
    }
}
