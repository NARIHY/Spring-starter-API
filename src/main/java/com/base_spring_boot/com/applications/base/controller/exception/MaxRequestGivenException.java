package com.base_spring_boot.com.applications.base.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MaxRequestGivenException extends ResponseStatusException {
    public MaxRequestGivenException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS,message);
    }
}
