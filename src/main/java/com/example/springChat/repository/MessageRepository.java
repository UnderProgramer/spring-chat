package com.example.springChat.repository;

import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.Messages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages, Long> {
    List<Messages> findByChatRoomIdAndMessageIdLessThanOrderByMessageIdDesc(ChatRoom room, Long cursorId, Pageable pageable);
    List<Messages> findByChatRoomIdOrderByMessageIdDesc(ChatRoom room, Pageable pageable);
}
