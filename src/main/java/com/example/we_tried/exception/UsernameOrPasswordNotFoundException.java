package com.example.we_tried.exception;

public class UsernameOrPasswordNotFoundException extends RuntimeException {
    public UsernameOrPasswordNotFoundException(String message) {
        super(message);
    }
    public UsernameOrPasswordNotFoundException() {}
}
