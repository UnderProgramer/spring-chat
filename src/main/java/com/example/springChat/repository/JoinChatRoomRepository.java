package com.example.springChat.repository;

import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.JoinChatRoom;
import com.example.springChat.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoinChatRoomRepository extends JpaRepository<JoinChatRoom, Long> {
    void deleteByChatRoomAndUser(ChatRoom chatRoom, Users user);
    Optional<JoinChatRoom> findByChatRoomAndUser(ChatRoom chatRoom, Users users);
    Page<JoinChatRoom> findByUser(Users users, Pageable pageable);
    JoinChatRoom findByChatRoom(ChatRoom chatRoom);
}
