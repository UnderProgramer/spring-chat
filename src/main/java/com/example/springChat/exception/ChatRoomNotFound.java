package com.example.springChat.exception;

public class ChatRoomNotFound extends RuntimeException {
    public ChatRoomNotFound(String message) {
        super(message);
    }
}
