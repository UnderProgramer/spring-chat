package com.example.springChat.service;

import com.example.springChat.dto.DiscordMessageDTO;
import com.example.springChat.dto.ReportRequestDTO;
import com.example.springChat.entity.ChatRoom;
import com.example.springChat.entity.Reports;
import com.example.springChat.entity.Users;
import com.example.springChat.exception.ChatRoomNotFound;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.repository.ChatRoomRepository;
import com.example.springChat.repository.ReportsRepository;
import com.example.springChat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportsService {
    private final ChatRoomRepository chatRoomRepository;
    private final ReportsRepository reportsRepository;
    private final UserRepository userRepository;

    private final WebClient reportsWebClient;

    @Value("${spring.discord.webhook}")
    private String webHookUrl;

    public void sendDiscordMessage(DiscordMessageDTO dto){
        var msg =
                "## 신고 알람 : " + dto.getTitle()
                        + "\n```신고자 : "
                        + dto.getUsername()
                        + "\n연락처 : "
                        + dto.getEmail()
                        + "\n\n사유 : "
                        + dto.getReason()
                        + "\n\n신고된 방 : "
                        + dto.getRoomTitle()
                        + "\n방 코드 : " + dto.getRoomCode()
                        + "```";

        reportsWebClient.post()
                .uri(webHookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", msg))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void sendDiscordReport(ReportRequestDTO dto, String username){
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자를 찾는 과정에서 오류가 났습니다."));
        ChatRoom chatRoom = chatRoomRepository.findByRoomCode(dto.getRoomCode())
                .orElseThrow(() -> new ChatRoomNotFound("방을 찾는 과정에서 오류가 났습니다."));

        DiscordMessageDTO discordMessage = DiscordMessageDTO.builder()
                .title(dto.getTitle())
                .roomTitle(chatRoom.getTitle())
                .reason(dto.getReason())
                .username(username)
                .email(user.getEmail())
                .roomCode(dto.getRoomCode())
                .build();

        sendDiscordMessage(discordMessage);

        Reports report = Reports.builder()
                .title(dto.getTitle())
                .reason(dto.getReason())
                .reporter(user)
                .chatRoom(chatRoom)
                .build();

        reportsRepository.save(report);
    }
}
