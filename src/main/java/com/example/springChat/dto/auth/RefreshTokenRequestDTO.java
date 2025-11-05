package com.example.springChat.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
