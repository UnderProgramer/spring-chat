package com.example.springChat.dto.auth.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUserDTO {
    private String password;
    private String patchUsername;
    private String patchEmail;
}
