package com.example.springChat;

import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.Messages;
import com.example.springChat.entity.Users;
import com.example.springChat.repository.ChatRoomRepository;
import com.example.springChat.repository.MessageRepository;
import com.example.springChat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DummyDataTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository; // 실제 레포지토리명 확인해줘요

    @Test
    void insertDummyMessages() {
        ChatRoom room = chatRoomRepository.findById(1L)
                .orElseThrow();

        Users user = userRepository.findById(1L)
                .orElseThrow();

        List<String> sampleMessages = List.of(
                "안녕하세요!",
                "오늘 날씨 좋다",
                "프로젝트 언제 끝나요?",
                "코드 리뷰 부탁드립니다",
                "점심 뭐 먹을까요?",
                "회의 몇 시예요?",
                "수고하셨습니다!",
                "내일 봐요~",
                "잠깐 통화 가능해요?",
                "확인했습니다 감사합니다"
        );

        List<Messages> messages = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            Messages msg = Messages.builder()
                    .author(user)
                    .message(sampleMessages.get(i % sampleMessages.size()))
                    .chatRoomId(room)
                    .build();
            messages.add(msg);
        }
        messageRepository.saveAll(messages);
        System.out.println("✅ 더미 데이터 5000개 삽입 완료!");
    }
}