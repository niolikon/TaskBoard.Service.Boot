package com.niolikon.taskboard.service.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TodoView {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("IsCompleted")
    private Boolean isCompleted;

    @JsonProperty("DueDate")
    private Date dueDate;
}
