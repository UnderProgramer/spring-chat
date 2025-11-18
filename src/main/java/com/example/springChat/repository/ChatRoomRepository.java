package com.example.springChat.repository;

import com.example.springChat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomCode(String roomCode);
    Optional<ChatRoom> findByRoomCodeAndIsDeletedFalse(String roomCode);

}
