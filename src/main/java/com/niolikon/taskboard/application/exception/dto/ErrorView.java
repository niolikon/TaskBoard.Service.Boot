package com.niolikon.taskboard.application.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorView {
    private int status;
    private String message;
}
