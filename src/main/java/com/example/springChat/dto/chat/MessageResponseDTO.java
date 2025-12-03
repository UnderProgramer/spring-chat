package com.example.springChat.dto.chat;

import com.example.springChat.entity.Messages;
import com.example.springChat.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponseDTO {
    private Long id;
    private String message;
    private String username;
    private LocalDateTime dateTime;

    public static MessageResponseDTO from(Messages message) {
        return MessageResponseDTO.builder()
                .id(message.getMessageId())
                .message(message.getMessage())
                .username(message.getAuthor().getUsername())
                .dateTime(message.getCreatedAt())
                .build();
    }
}
