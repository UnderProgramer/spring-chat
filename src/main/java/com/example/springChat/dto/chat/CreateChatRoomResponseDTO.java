package com.example.springChat.dto.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChatRoomResponseDTO {
    private String roomCode;
}
