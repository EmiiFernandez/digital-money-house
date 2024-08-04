package com.dmh.user_service.exception;

public class ErrorResponse extends Exception {

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        super(message);
    }
}
