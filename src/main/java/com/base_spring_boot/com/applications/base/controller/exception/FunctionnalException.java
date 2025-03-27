package com.base_spring_boot.com.applications.base.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class FunctionnalException extends HttpStatusCodeException {



    public FunctionnalException(String message) {
        super(HttpStatus.BAD_REQUEST,message);
    }

}
