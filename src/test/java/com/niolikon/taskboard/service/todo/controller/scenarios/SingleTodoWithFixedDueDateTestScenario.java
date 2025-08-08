package com.niolikon.taskboard.service.todo.controller.scenarios;

import com.niolikon.taskboard.service.todo.model.Todo;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class SingleTodoWithFixedDueDateTestScenario {
    public static final String USER_UUID = "user_uuid";
    public static final Date FIXED_DUE_DATE = Date.from(
            LocalDate.of(2025,3, 29)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
    );
    public static final String FIXED_DUE_DATE_FORMATTED = "2025-03-29";

    public static List<Object> getDataset() {
        return List.of(
                Todo.builder()
                        .title("Example Todo")
                        .description("Example description")
                        .dueDate(FIXED_DUE_DATE)
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build()
        );
    }
}
