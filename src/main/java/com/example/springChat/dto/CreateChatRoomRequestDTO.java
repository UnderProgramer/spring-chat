package com.example.springChat.dto;

import lombok.*;

@Setter
@Getter
public class CreateChatRoomRequestDTO {
    private String roomTitle;
    private String roomDescription;
}
