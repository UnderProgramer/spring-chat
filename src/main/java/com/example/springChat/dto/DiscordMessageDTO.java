package com.example.springChat.dto;

import com.example.springChat.entity.Users;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordMessageDTO {
    private String title;
    private String reason;
    private String username;
    private String email;

    private String roomCode;
    private String roomTitle;
}
