package com.example.springChat.dto.auth;

import com.example.springChat.entity.Users;
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
