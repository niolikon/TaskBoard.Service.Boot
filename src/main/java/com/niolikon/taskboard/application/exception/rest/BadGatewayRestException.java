package com.niolikon.taskboard.application.exception.rest;

import org.springframework.http.HttpStatus;

public class BadGatewayRestException extends RestException {
    public BadGatewayRestException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}
