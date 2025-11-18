package com.example.springChat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChatRoomResponseDTO {
    private String roomCode;
}
