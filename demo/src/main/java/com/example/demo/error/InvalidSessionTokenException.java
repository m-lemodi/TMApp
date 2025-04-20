package com.example.demo.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidSessionTokenException extends RuntimeException {
    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
