package com.example.springChat.controller;

import com.example.springChat.dto.ApiResponse;
import com.example.springChat.dto.CreateChatRoomRequestDTO;
import com.example.springChat.dto.CreateChatRoomResponseDTO;
import com.example.springChat.dto.GetChatRoomResponseDTO;
import com.example.springChat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller()
@AllArgsConstructor
@Slf4j
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    @MessageMapping("/chat/{roomCode}")
    public void sendMessage(@DestinationVariable String roomCode , String message) {
        //String destination = "/topic/" + roomCode + "/chat";
        //메시지 /app/chat/1231로 보내야함
        String destination = "/topic/1231/chat";
        messagingTemplate.convertAndSend(destination, message);
        chatService.saveMessage(roomCode, message);
    }

    @PostMapping("/chat/room")
    public ResponseEntity<ApiResponse<CreateChatRoomResponseDTO>> createRoom(@RequestBody CreateChatRoomRequestDTO dto) {
        CreateChatRoomResponseDTO result = chatService.create(dto);
        return ResponseEntity.ok(ApiResponse.ok(result, "방 생성 성공"));
    }

    @GetMapping("/chat/room")
    public ResponseEntity<ApiResponse<Page<GetChatRoomResponseDTO>>> getAllRoom(Pageable pageable) {
        Page<GetChatRoomResponseDTO> result = chatService.allRoomChat(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result, "모든 방 조회 성공"));
    }
}
