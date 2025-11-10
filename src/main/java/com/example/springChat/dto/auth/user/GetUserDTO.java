package com.example.springChat.dto.auth.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserDTO {
    private String email;
    private String username;
}
