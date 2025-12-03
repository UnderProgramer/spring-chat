package com.example.springChat.controller;

import com.example.springChat.dto.ApiResponse;
import com.example.springChat.dto.CursorResponse;
import com.example.springChat.dto.chat.MessageResponseDTO;
import com.example.springChat.dto.chat.*;
import com.example.springChat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/room")
@AllArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateChatRoomResponseDTO>> createRoom(@RequestBody CreateChatRoomRequestDTO dto) {
        CreateChatRoomResponseDTO result = chatService.create(dto);
        return ResponseEntity.ok(ApiResponse.ok(result, "방 생성 성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetChatRoomResponseDTO>>> getAllRoom(Pageable pageable) {
        Page<GetChatRoomResponseDTO> result = chatService.allChatRoom(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result, "모든 방 조회 성공"));
    }
    @GetMapping("/joinedRoom")
    public ResponseEntity<ApiResponse<Page<JoinChatRoomDTO>>> getJoinedRoom(Pageable pageable){
        Page<JoinChatRoomDTO> result = chatService.joinedChatRoom(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result, "해당 사용자가 참여한 방 조회"));
    }

    @GetMapping("/{roomCode}/message")
    public ResponseEntity<CursorResponse<MessageResponseDTO>> getMessages(@RequestParam Long cursorId,
                                                                          @PathVariable String roomCode,
                                                                          @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(chatService.getMessages(cursorId ,roomCode ,userDetails.getUsername()));
    }

    @PutMapping("/{roomCode}/message/read")
    public ResponseEntity<String> saveLastMessage(@RequestBody LastMessageDTO dto,
                                                  @PathVariable String roomCode,
                                                  @AuthenticationPrincipal UserDetails userDetails){
        chatService.saveLastReadMessage(dto, roomCode, userDetails.getUsername());
        return ResponseEntity.ok("메시지 읽기 성공");

    }

    @DeleteMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable String roomCode) throws AccessDeniedException {
        chatService.delete(roomCode);
        return ResponseEntity.ok(ApiResponse.ok("방 삭제 성공"));
    }

    @PostMapping("/{roomCode}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(@PathVariable String roomCode,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        chatService.leaveRoom(roomCode, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("방을 떠났습니다."));
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<ApiResponse<Void>> joinRoom(@PathVariable String roomCode,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        chatService.joinRoom(roomCode, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("방에 입장했습니다."));
    }

    @PostMapping("/{roomCode}/invite")
    public ResponseEntity<ApiResponse<InviteChatRoomResponseDTO>> inviteRoom(@RequestBody InviteChatRoomDTO dto,
                                                        @PathVariable String roomCode,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        InviteChatRoomResponseDTO result = chatService.inviteChatRoom(dto, roomCode, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(result, "초대 성공"));
    }
}
