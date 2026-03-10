package com.m347.pollit.exceptions;

import org.springframework.http.HttpStatus;

public class CommonException extends RuntimeException {
    public HttpStatus status;
    public CommonException(String message) {
        super(message);
    }
    public CommonException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
