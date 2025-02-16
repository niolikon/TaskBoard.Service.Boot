package com.niolikon.taskboard.application.exception;

import com.niolikon.taskboard.application.exception.dto.ErrorView;
import com.niolikon.taskboard.application.exception.rest.RestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorView> handleRestException(RestException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorView(ex.getStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorView> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorView(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
}
