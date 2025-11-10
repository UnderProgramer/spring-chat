package com.example.springChat.exception;

public class AlreadyExistsUsername extends RuntimeException {
    public AlreadyExistsUsername(String message) {

        super(message);
    }
}
