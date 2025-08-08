package com.niolikon.taskboard.service.todo.controller.testdata;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class TodoControllerTestData {
    private TodoControllerTestData() {}

    public static final String VALID_USER_ID = "test-user-id";
    public static final String VALID_USER_ROLE = "ROLE_USER";
    public static final String INVALID_USER_ROLE = "ROLE_TESTER";
    public static final String JWT_SUBJECT_VALID_USER_ID = VALID_USER_ID;
    public static final Long VALID_TODO_ASSIGNED_ID = 42L;
    public static final String VALID_TODO_TITLE = "Title";
    public static final String VALID_TODO_DESCRIPTION = "Sample description";
    public static final Instant INSTANT_IN_THE_FUTURE = Instant.now().plus(2, ChronoUnit.DAYS);
    public static final Instant INSTANT_IN_THE_PAST = Instant.now().minus(10, ChronoUnit.SECONDS);

    public static final TodoRequest todoRequest_valid_fromClient = new TodoRequest(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_expected_fromTodoRequest = new TodoView(VALID_TODO_ASSIGNED_ID, VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));

    public static final TodoPatch todoPatch_valid_fromClient = new TodoPatch(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_expected_fromTodoPatch = new TodoView(VALID_TODO_ASSIGNED_ID, VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));

    public static final TodoPatch todoPatch_done_fromClient = new TodoPatch(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, Boolean.TRUE, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_expected_fromTodoPatchDone = new TodoView(VALID_TODO_ASSIGNED_ID, VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, Boolean.TRUE, Date.from(INSTANT_IN_THE_FUTURE));

    public static final TodoView todoView_instance1_fromRepository = new TodoView(1L, "Task 1", "Description 1", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_instance2_fromRepository = new TodoView(2L, "Task 2", "Description 2", Boolean.TRUE, Date.from(INSTANT_IN_THE_FUTURE));

    public static final TodoView todoView_pending1_fromRepository = new TodoView(1L, "Title", "A Description", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_pending2_fromRepository = new TodoView(5L, "Title", "Another Description", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE));

    public static final Pageable pageable_firstPageSize10_fromClient = PageRequest.of(0, 10);
    public static final PageResponse<TodoView> pageResponseTodoView_empty_fromRepository = new PageResponse<>(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

    public static final RequestPostProcessor jwtRequest_withoutAuthorities = jwt().jwt(jwt -> jwt.subject(VALID_USER_ID));
    public static final RequestPostProcessor jwtRequest_withInvalidRole = jwt().jwt(jwt -> jwt.subject(VALID_USER_ID))
            .authorities(new SimpleGrantedAuthority(INVALID_USER_ROLE));
    public static final RequestPostProcessor jwtRequest_withValidRole = jwt()
            .jwt(jwt -> jwt.subject(VALID_USER_ID))
            .authorities(new SimpleGrantedAuthority(VALID_USER_ROLE));
}
