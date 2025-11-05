package com.example.springChat.dto.auth;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponseDTO {
    private String accessToken;
}
