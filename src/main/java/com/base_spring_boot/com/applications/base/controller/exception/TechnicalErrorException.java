package com.base_spring_boot.com.applications.base.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TechnicalErrorException extends ResponseStatusException {
    public TechnicalErrorException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR,message);
    }
}
