package com.niolikon.taskboard.service.todo.service.testdata;

import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.model.Todo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TodoServiceTestData {
    public static final String VALID_OWNER_UID = "test-user-123";
    public static final Long VALID_EXISTENT_TODO_ID = 1L;
    public static final Long VALID_NON_EXISTENT_TODO_ID = 111L;

    public static final Instant INSTANT_IN_THE_FUTURE = Instant.now().plus(2, ChronoUnit.DAYS);

    public static final Todo todo_instance1_fromRepository = new Todo(1L, "Task 1", "Desc 1", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE), VALID_OWNER_UID);
    public static final Todo todo_instance2_fromRepository = new Todo(2L, "Task 2", "Desc 2", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE), VALID_OWNER_UID);
    public static final Todo todo_instance3_fromRepository = new Todo(3L, "Task 3", "Desc 3", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE), VALID_OWNER_UID);
    public static final Todo todo_instance4_fromRepository = new Todo(4L, "Task 4", "Desc 4", Boolean.FALSE, Date.from(INSTANT_IN_THE_FUTURE), VALID_OWNER_UID);

    public static final TodoView todoView_mapped1_fromTodoInstance = new TodoView(1L, "Task 1", "Desc 1", false, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_mapped2_fromTodoInstance = new TodoView(2L, "Task 2", "Desc 2", false, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_mapped3_fromTodoInstance = new TodoView(3L, "Task 3", "Desc 3", false, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_mapped4_fromTodoInstance = new TodoView(4L, "Task 4", "Desc 4", false, Date.from(INSTANT_IN_THE_FUTURE));

    public static final TodoRequest todoRequest_valid_fromClient = TodoRequest.builder()
            .title("New Task").description("Task Description").isCompleted(false).build();
    public static final Todo todo_valid_fromTodoRequest = Todo.builder()
            .title("New Task").description("Task Description").isCompleted(false).ownerUid(VALID_OWNER_UID).build();
    public static final Todo todo_saved_fromTodoRequest = Todo.builder()
            .title("New Task").description("Task Description").isCompleted(false).ownerUid(VALID_OWNER_UID).id(VALID_EXISTENT_TODO_ID).build();
    public static final TodoView todoView_expected_fromSavedTodo = TodoView.builder()
            .title("New Task").description("Task Description").isCompleted(false).id(VALID_EXISTENT_TODO_ID).build();

    public static final TodoView todoView_instance1_fromRepository = new TodoView(1L, "Task 1", "Desc 1", false, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_instance2_fromRepository = new TodoView(2L, "Task 2", "Desc 2", true, Date.from(INSTANT_IN_THE_FUTURE));

    public static final Todo todo_existing_fromRepository = Todo.builder()
            .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID).build();
    public static final TodoView todoView_expected_fromFoundTodo = TodoView.builder()
            .id(VALID_EXISTENT_TODO_ID).title("Existing Task").description("Existing Desc").build();

    public static final TodoRequest todoRequest_validUpdate_fromClient = TodoRequest.builder()
            .title("Updated Task").description("Updated Desc").build();
    public static final Todo todo_saved_fromTodoUpdateRequest = Todo.builder()
            .id(VALID_EXISTENT_TODO_ID).title("Updated Task").description("Updated Desc").ownerUid(VALID_OWNER_UID).build();
    public static final TodoView todoView_expected_fromUpdatedTodo = TodoView.builder()
            .id(VALID_EXISTENT_TODO_ID).title("Updated Task").description("Updated Desc").build();

    public static final TodoPatch todoPatch_completed_fromClient = TodoPatch.builder()
            .isCompleted(Boolean.TRUE)
            .build();

    public static final Todo todo_mapped_fromTodoPatch = Todo.builder()
            .isCompleted(Boolean.TRUE)
            .build();
    public static final Todo todo_existingNonCompleted_fromRepository = Todo.builder()
            .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
            .title("Title").description("Description").isCompleted(Boolean.FALSE).dueDate(Date.from(INSTANT_IN_THE_FUTURE))
            .build();
    public static final Todo todo_patchedAndSaved_fromRepository = Todo.builder()
            .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
            .title(todo_existingNonCompleted_fromRepository.getTitle())
            .description(todo_existingNonCompleted_fromRepository.getDescription())
            .isCompleted(todo_existingNonCompleted_fromRepository.getIsCompleted())
            .dueDate(todo_existingNonCompleted_fromRepository.getDueDate())
            .build();
    public static final TodoView todoView_mapped_fromPatchedTodo = TodoView.builder()
            .id(VALID_EXISTENT_TODO_ID)
            .title(todo_patchedAndSaved_fromRepository.getTitle())
            .description(todo_patchedAndSaved_fromRepository.getDescription())
            .isCompleted(todo_patchedAndSaved_fromRepository.getIsCompleted())
            .dueDate(todo_patchedAndSaved_fromRepository.getDueDate())
            .build();

    public static final Todo todo_existingCompleted_fromRepository = Todo.builder()
            .id(VALID_EXISTENT_TODO_ID).ownerUid(VALID_OWNER_UID)
            .title("Title").description("Description").isCompleted(Boolean.TRUE).dueDate(Date.from(INSTANT_IN_THE_FUTURE))
            .build();
    public static final TodoView todoView_mapped_fromExistingTodo = TodoView.builder()
            .id(VALID_EXISTENT_TODO_ID)
            .title(todo_existingCompleted_fromRepository.getTitle())
            .description(todo_existingCompleted_fromRepository.getDescription())
            .isCompleted(todo_existingCompleted_fromRepository.getIsCompleted())
            .dueDate(todo_existingCompleted_fromRepository.getDueDate())
            .build();

    public static final TodoRequest todoRequest_modifiedTitleAndCompletion_fromClient = TodoRequest.builder()
            .title("Updated Title").isCompleted(Boolean.TRUE)
            .build();

    public static final Todo todo_pending1_fromRepository = new Todo();
    public static final Todo todo_pending2_fromRepository = new Todo();
    public static final TodoView todoView_mapped1_fromPendingTodo = new TodoView(1L, "Task 1", "Desc 1", false, Date.from(INSTANT_IN_THE_FUTURE));
    public static final TodoView todoView_mapped2_fromPendingTodo = new TodoView(2L, "Task 2", "Desc 2", false, Date.from(INSTANT_IN_THE_FUTURE));

    public static final Pageable pageable_firstPageSize10_fromClient = PageRequest.of(0, 10);
    public static final Pageable pageable_firstPageSize2_fromClient = PageRequest.of(0, 2);
    public static final Pageable pageable_secondPageSize2_fromClient = PageRequest.of(1, 2);
}
