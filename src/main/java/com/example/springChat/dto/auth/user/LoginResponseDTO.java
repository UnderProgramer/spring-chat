package com.example.springChat.dto.auth.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
}
