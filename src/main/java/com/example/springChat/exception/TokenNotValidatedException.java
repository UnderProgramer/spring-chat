package com.example.springChat.exception;

public class TokenNotValidatedException extends RuntimeException {
    public TokenNotValidatedException(String message) {
        super(message);
    }
}
