package com.example.springChat.service;

import com.example.springChat.dto.CreateChatRoomRequestDTO;
import com.example.springChat.dto.CreateChatRoomResponseDTO;
import com.example.springChat.dto.GetChatRoomResponseDTO;
import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.Messages;
import com.example.springChat.entity.Users;
import com.example.springChat.exception.ChatRoomNotFound;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.repository.ChatRoomRepository;
import com.example.springChat.repository.UserRepository;
import com.example.springChat.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    private final String username = SecurityContextHolder.getContext().getAuthentication().getName();

    private static String generateCode() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int idx = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(idx));
        }
        return sb.toString();
    }

    public CreateChatRoomResponseDTO create(CreateChatRoomRequestDTO dto) {
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
    public void delete(String roomCode, Pageable pageable) throws AccessDeniedException {
        ChatRoom chatRoom = chatRoomRepository.findByRoomCodeAndIsDeletedFalseAndIsPrivateFalse(pageable, roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("방을 찾을 수 없습니다."));

        if (!chatRoom.getOwner().getUsername().contains(username)){
            throw new AccessDeniedException("이 채팅방을 삭제할 수 없습니다");
        }

        chatRoom.setDeleted(true);
    }

    public Page<GetChatRoomResponseDTO> allRoomChat(Pageable pageable) {
        return chatRoomRepository.findAllAndIsDeletedFalseAndIsPrivateFalse(pageable)
                .map(GetChatRoomResponseDTO::from);
    }

    public Page<>

    public void saveMessage(String message, String roomCode) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("메시지를 보낸 방이 잘못 되었습니다."));

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자가 잘못 되었습니다."));

        Messages messages = Messages.builder()
                .message(message)
                .chatRoomId(chatRoom)
                .author(user)
                .build();

        messageRepository.save(messages);
    }

}
