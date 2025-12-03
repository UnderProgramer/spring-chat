package com.example.springChat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    private String title;
    private String reason;
    private String roomCode;
}

