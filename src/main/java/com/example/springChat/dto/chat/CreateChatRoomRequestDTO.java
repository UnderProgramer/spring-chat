package com.example.springChat.dto.chat;

import lombok.*;

@Setter
@Getter
public class CreateChatRoomRequestDTO {
    private String roomTitle;
    private String roomDescription;
    private boolean isPrivate;
}
