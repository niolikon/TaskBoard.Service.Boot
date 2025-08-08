package com.niolikon.taskboard.service.todo.controller.scenarios;

import com.niolikon.taskboard.service.todo.model.Todo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class MultipleTodosWithPaginationTestScenario {
    public static final String USER_UUID = "user_uuid";
    public static final Date DUE_DATE = Date.from(
            LocalDate.of(2025,3, 29)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
    );

    public static List<Object> getDataset() {
        return List.of(
                Todo.builder()
                        .title("Example Todo 1")
                        .description("Example description 1")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 2")
                        .description("Example description 2")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 3")
                        .description("Example description 3")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 4")
                        .description("Example description 4")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 5")
                        .description("Example description 5")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 6")
                        .description("Example description 6")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 7")
                        .description("Example description 7")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 8")
                        .description("Example description 8")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 9")
                        .description("Example description 9")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 10")
                        .description("Example description 10")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 11")
                        .description("Example description 11")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 12")
                        .description("Example description 12")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 13")
                        .description("Example description 13")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 14")
                        .description("Example description 14")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build(),
                Todo.builder()
                        .title("Example Todo 15")
                        .description("Example description 15")
                        .dueDate(DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build()
        );
    }

    public static final long DATASET_ELEMENTS_TOTAL = getDataset().size();

    public static final Pageable SAMPLE_QUERY1 = PageRequest.of(0, 5);
    public static final int SAMPLE_QUERY1_ELEMENTS_SIZE = 5;
    public static final int SAMPLE_QUERY1_PAGE_TOTAL = 3;
    public static final boolean SAMPLE_QUERY1_FIRST = true;
    public static final boolean SAMPLE_QUERY1_LAST = false;
    public static final boolean SAMPLE_QUERY1_EMPTY = false;

    public static final Pageable SAMPLE_QUERY2 = PageRequest.of(2, 7);
    public static final int SAMPLE_QUERY2_ELEMENTS_SIZE = 1;
    public static final int SAMPLE_QUERY2_PAGE_TOTAL = 3;
    public static final boolean SAMPLE_QUERY2_FIRST = false;
    public static final boolean SAMPLE_QUERY2_LAST = true;
    public static final boolean SAMPLE_QUERY2_EMPTY = false;

}
