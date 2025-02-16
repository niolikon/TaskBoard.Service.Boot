package com.niolikon.taskboard.application.exception.rest;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
