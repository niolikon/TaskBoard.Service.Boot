package com.niolikon.taskboard.application.exception.rest;

import org.springframework.http.HttpStatus;

public class EntityNotFoundRestException extends RestException {
    public EntityNotFoundRestException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
