package com.niolikon.taskboard.domain.todo.scenario;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.niolikon.taskboard.domain.todo.model.Todo;

public class SingleTodoTestScenario {
    public static final String USER_UUID = "user_uuid";

    public static List<Object> getDataset() {
        return List.of(
                Todo.builder()
                        .title("Example Todo")
                        .description("Example description")
                        .dueDate(Date.from(Instant.now()))
                        .isCompleted(Boolean.FALSE)
                        .ownerUid(USER_UUID)
                        .build()
        );
    }
}
