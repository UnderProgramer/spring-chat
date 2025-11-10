package com.example.springChat.dto.auth.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchPasswordDTO {
    private String originPassword;
    private String patchPassword;
}
