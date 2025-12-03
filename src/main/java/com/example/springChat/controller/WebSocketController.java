package com.example.springChat.controller;

import com.example.springChat.dto.chat.MessageDTO;
import com.example.springChat.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@AllArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/{roomCode}")
    public void sendMessage(@DestinationVariable String roomCode,
                            @Payload MessageDTO message,
                            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String destination = "/topic/" + roomCode + "/chat";

        String username = (String) sessionAttributes.get("username");

        chatService.saveMessage(message, roomCode, username);
        messagingTemplate.convertAndSend(destination, message.getMessage());
    }
}
