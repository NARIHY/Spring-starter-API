package com.base_spring_boot.com.tmoto.base.controller.exception;

public class FunctionnalException extends  RuntimeException{
    private final int statusCode;


    public FunctionnalException(String message) {
        super(message);
        this.statusCode = 400;
    }

    // Getter pour le code de statut
    public int getStatusCode() {
        return statusCode;
    }
}
