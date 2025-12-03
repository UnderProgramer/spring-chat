package com.example.springChat.dto.chat;

import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.JoinChatRoom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinChatRoomDTO {
    private String roomTitle;
    private String roomDescription;
    private String roomCode;

    public static JoinChatRoomDTO from(JoinChatRoom entity) {
        return JoinChatRoomDTO.builder()
                .roomTitle(entity.getChatRoom().getTitle())
                .roomDescription(entity.getChatRoom().getDescription())
                .roomCode(entity.getChatRoom().getRoomCode())
                .build();
    }
}
