package com.example.springChat.dto;

import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.Users;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRoomChatResponseDTO {
    private String roomTitle;
    private String roomDescription;
    private String ownerName;
    private boolean isPrivate;

    public static GetRoomChatResponseDTO from(ChatRoom entity) {
        return GetRoomChatResponseDTO.builder()
                .roomTitle(entity.getTitle())
                .roomDescription(entity.getDescription())
                .ownerName(entity.getOwner().getUsername())
                .isPrivate(entity.isPrivate())
                .build();
    }
}
