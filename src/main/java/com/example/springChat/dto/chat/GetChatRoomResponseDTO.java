package com.example.springChat.dto.chat;

import com.example.springChat.entity.ChatRoom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetChatRoomResponseDTO {
    private String roomTitle;
    private String roomDescription;
    private String ownerName;
    private String roomCode;

    public static GetChatRoomResponseDTO from(ChatRoom entity) {
        return GetChatRoomResponseDTO.builder()
                .roomTitle(entity.getTitle())
                .roomDescription(entity.getDescription())
                .ownerName(entity.getUserId().getUsername())
                .roomCode(entity.getRoomCode())
                .build();
    }
}
