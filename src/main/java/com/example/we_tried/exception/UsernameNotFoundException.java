package com.example.we_tried.exception;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
    public UsernameNotFoundException() {}
}
