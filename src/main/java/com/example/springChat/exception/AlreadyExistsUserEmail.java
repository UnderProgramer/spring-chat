package com.example.springChat.exception;

public class AlreadyExistsUserEmail extends RuntimeException {
    public AlreadyExistsUserEmail(String message) {

        super(message);
    }
}
