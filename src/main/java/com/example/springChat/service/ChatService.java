package com.example.springChat.service;

import com.example.springChat.dto.CreateChatRoomRequestDTO;
import com.example.springChat.dto.CreateChatRoomResponseDTO;
import com.example.springChat.dto.GetRoomChatResponseDTO;
import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.Users;
import com.example.springChat.exception.ChatRoomNotFound;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.repository.ChatRoomRepository;
import com.example.springChat.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    private static String generateCode() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int idx = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(idx));
        }
        return sb.toString();
    }

    public CreateChatRoomResponseDTO create(CreateChatRoomRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String roomCode = generateCode();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자를 찾을 수 없습니다"));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomCode(roomCode)
                .title(dto.getRoomTitle())
                .owner(user)
                .description(dto.getRoomDescription())
                .build();

        chatRoomRepository.save(chatRoom);

        return CreateChatRoomResponseDTO.builder()
                .roomCode(roomCode)
                .build();
    }

    @Transactional
    public void delete(String roomCode) throws AccessDeniedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatRoom chatRoom = chatRoomRepository.findByRoomCodeAndIsDeletedFalse(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("방을 찾을 수 없습니다."));

        if (!chatRoom.getOwner().getUsername().contains(username)){
            throw new AccessDeniedException("이 채팅방을 삭제할 수 없습니다");
        }

        chatRoom.setDeleted(true);
    }

    public List<GetRoomChatResponseDTO> allRoomChat() {
        return chatRoomRepository.findAll().stream()
                .map(GetRoomChatResponseDTO::from)
                .toList();
    }

}
