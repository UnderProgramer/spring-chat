package com.example.springChat.dto.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteChatRoomResponseDTO {
    private String title;
    private String inviterName;
    private String invitedName;
}
