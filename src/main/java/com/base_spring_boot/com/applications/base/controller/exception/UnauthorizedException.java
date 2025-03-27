package com.base_spring_boot.com.applications.base.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class UnauthorizedException extends HttpStatusCodeException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED,message);
    }
}
