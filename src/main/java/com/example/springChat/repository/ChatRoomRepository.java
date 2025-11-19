package com.example.springChat.repository;

import com.example.springChat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomCodeAndIsDeletedFalseAndIsPrivateFalse(Pageable pageable, String roomCode);
    Page<ChatRoom> findAllAndIsDeletedFalseAndIsPrivateFalse(Pageable pageable);
    Optional<ChatRoom> findByRoomCode(String roomCode);
}
