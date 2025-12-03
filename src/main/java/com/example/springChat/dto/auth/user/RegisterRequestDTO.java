package com.example.springChat.dto.auth.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {
    @Email
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
