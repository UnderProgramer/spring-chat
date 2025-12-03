package com.example.springChat.dto.chat;

import com.example.springChat.entity.ChatRoom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetChatRoomDetailByUserDTO {
    private String roomTitle;
    private String roomDescription;
    private String roomCode;
    private boolean privates;

    public static GetChatRoomDetailByUserDTO from(ChatRoom entity) {
        return GetChatRoomDetailByUserDTO.builder()
                .roomTitle(entity.getTitle())
                .roomDescription(entity.getDescription())
                .roomCode(entity.getRoomCode())
                .privates(entity.isPrivate())
                .build();
    }
}
