package com.niolikon.taskboard.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TodoRequest {
    @NotEmpty
    @Size(min = 3, max = 50)
    @JsonProperty("Title")
    private String title;

    @NotEmpty
    @Size(min = 4, max = 250)
    @JsonProperty("Description")
    private String description;

    @NotNull
    @JsonProperty("IsCompleted")
    private Boolean isCompleted;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("DueDate")
    private Date dueDate;
}
