package com.example.springChat.service;

import com.example.springChat.dto.CursorResponse;
import com.example.springChat.dto.chat.MessageResponseDTO;
import com.example.springChat.dto.chat.*;
import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.JoinChatRoom;
import com.example.springChat.entity.Messages;
import com.example.springChat.entity.Users;
import com.example.springChat.exception.AlreadyExistsUsername;
import com.example.springChat.exception.ChatRoomNotFound;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.repository.ChatRoomRepository;
import com.example.springChat.repository.JoinChatRoomRepository;
import com.example.springChat.repository.UserRepository;
import com.example.springChat.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.hibernate.mapping.Join;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final JoinChatRoomRepository joinChatRoomRepository;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    private static String generateCode(int max) {
        StringBuilder sb = new StringBuilder(max);
        for (int i = 0; i < 10; i++) {
            int idx = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(idx));
        }
        return sb.toString();
    }

    public CreateChatRoomResponseDTO create(CreateChatRoomRequestDTO dto) {
        String roomCode = null;
        if(dto.isPrivate()){
            roomCode = generateCode(15);
        }else{
            roomCode = generateCode(10);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자를 찾을 수 없습니다"));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomCode(roomCode)
                .title(dto.getRoomTitle())
                .userId(user)
                .description(dto.getRoomDescription())
                .isPrivate(dto.isPrivate())
                .build();

        chatRoomRepository.save(chatRoom);

        return CreateChatRoomResponseDTO.builder()
                .roomCode(roomCode)
                .build();
    }

    @Transactional
    public void delete(String roomCode) throws AccessDeniedException {
        ChatRoom chatRoom = chatRoomRepository.findByRoomCodeAndIsDeletedFalseAndIsPrivateFalse(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("방을 찾을 수 없습니다."));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!chatRoom.getUserId().getUsername().contains(username)){
            throw new AccessDeniedException("이 채팅방을 삭제할 수 없습니다");
        }

        chatRoom.setDeleted(true);
    }

    public Page<GetChatRoomResponseDTO> allChatRoom(Pageable pageable) {
        return chatRoomRepository.findByIsDeletedFalseAndIsPrivateFalse(pageable)
                .map(GetChatRoomResponseDTO::from);
    }

    public Page<JoinChatRoomDTO> joinedChatRoom(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자가 잘못 되었습니다."));

        return joinChatRoomRepository.findByUser(user, pageable)
                .map(JoinChatRoomDTO::from);

    }

    public Page<GetChatRoomDetailByUserDTO> detailChatRoomByUser(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자가 잘못 되었습니다."));

        return chatRoomRepository.findByUserIdAndIsDeletedFalseAndIsPrivateFalse(pageable, user)
                .map(GetChatRoomDetailByUserDTO::from);
    }


    public void saveMessage(MessageDTO message, String roomCode, String username) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("메시지를 보낸 방이 잘못 되었습니다."));

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자가 잘못 되었습니다."));

        Messages messages = Messages.builder()
                .message(message.getMessage())
                .chatRoomId(chatRoom)
                .author(user)
                .build();

        messageRepository.save(messages);
    }

    @Transactional(readOnly = true)
    public CursorResponse<MessageResponseDTO> getMessages(Long cursorId, String roomCode, String username) {
        int pageSize = 10;

        ChatRoom room = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("잘못된 방에서 메시지를 요청함"));

        Pageable pageable = PageRequest.of(0, pageSize + 1);
        Long nextCursor = null;

        List<Messages> messages;

        if (cursorId == null) {
            messages = messageRepository.findByChatRoomIdOrderByMessageIdDesc(room, pageable);
        } else {
            messages = messageRepository.findByChatRoomIdAndMessageIdLessThanOrderByMessageIdDesc(
                    room,
                    cursorId,
                    pageable
            );
        }

        boolean hasNext = messages.size() > pageSize;
        List<Messages> limited = hasNext ? messages.subList(0, pageSize) : messages;
        if(limited.isEmpty()){
            return new CursorResponse<>(null, null, false);
        }

        List<MessageResponseDTO> dtoList = limited.stream()
                .map(MessageResponseDTO::from)
                .collect(Collectors.toList());

        if (hasNext) {
            nextCursor = limited.get(limited.size() - 1).getMessageId();
        }

        return new CursorResponse<>(
                dtoList,
                nextCursor,
                hasNext
        );
    }

    public void joinRoom(String username, String roomCode) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("잘못된 사용자"));
        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("잘못된 방 코드"));

        JoinChatRoom joinChatRoom = JoinChatRoom.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();

        joinChatRoomRepository.save(joinChatRoom);
    }

    public void leaveRoom(String username, String roomCode) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("잘못된 사용자"));
        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("잘못된 방 코드"));

        joinChatRoomRepository.deleteByChatRoomAndUser(chatRoom, user);
    }

    @Transactional
    public void saveLastReadMessage(LastMessageDTO dto, String roomCode, String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("메시지 사용자를 찾을 수 없습니다"));
        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("채팅방을 찾을수 없습니다."));

        JoinChatRoom joinChatRoom = joinChatRoomRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 가입한 해당 방이 없습니다."));
        Messages message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을수 없습니다"));

        joinChatRoom.setLastMessageRead(message);
    }

    public InviteChatRoomResponseDTO inviteChatRoom(InviteChatRoomDTO dto, String roomCode, String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("초대자가 없습니다."));
        Users invitedUser = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFound("초대할 사용자가 존재 하지 않습니다"));

        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ChatRoomNotFound("초대 하려는 방이 존재 하지 않습니다."));


        joinChatRoomRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new NotFoundException("초대자의 방과 초대자가 잘못되었습니다."));

        if(user == invitedUser){
            throw new AlreadyExistsUsername("초대자와 초대할 사람이 같을 수 없습니다.");
        }

        JoinChatRoom joinChatRoom = JoinChatRoom.builder()
                .chatRoom(chatRoom)
                .user(invitedUser)
                .build();

        joinChatRoomRepository.save(joinChatRoom);

        return InviteChatRoomResponseDTO.builder()
                .invitedName(invitedUser.getUsername())
                .inviterName(user.getUsername())
                .title(chatRoom.getTitle())
                .build();
    }

}
