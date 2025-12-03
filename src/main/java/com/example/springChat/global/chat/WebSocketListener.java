package com.example.springChat.global.chat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.nio.file.AccessDeniedException;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketListener {

    @EventListener
    public void handleWebSocketSubscribe(SessionSubscribeEvent event) throws AccessDeniedException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = headerAccessor.getDestination();
        if (destination == null) return;

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username == null) {
            System.out.println("인증 되지 않은 사용자의 요청");
            throw new  AccessDeniedException("사용자가 인증 되지 않았습니다.");
        }
        log.info("{}구독 완료", username);

    }
}

