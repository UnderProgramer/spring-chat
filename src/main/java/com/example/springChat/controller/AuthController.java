package com.example.springChat.controller;

import com.example.springChat.dto.ApiResponse;
import com.example.springChat.dto.auth.token.RefreshTokenRequestDTO;
import com.example.springChat.dto.auth.token.RefreshTokenResponseDTO;
import com.example.springChat.dto.auth.user.*;
import com.example.springChat.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequestDTO dto) {
        userService.registerService(dto);
        return ResponseEntity.ok(ApiResponse.ok("회원 가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO result =  userService.loginService(dto);
        return ResponseEntity.ok(ApiResponse.ok(result,"로그인 성공"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> refresh(@RequestBody RefreshTokenRequestDTO dto) {
        RefreshTokenResponseDTO result = userService.refresh(dto);
        return ResponseEntity.ok(ApiResponse.ok(result, "토큰 재발급 성공"));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody PatchPasswordDTO dto){
        userService.patchPassword(dto);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호변경 성공"));
    }

    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> changeUserInfo(@RequestBody PatchUserDTO dto) {
        RefreshTokenResponseDTO result = userService.patchUser(dto);
        return ResponseEntity.ok(ApiResponse.ok(result, "사용자 정보 변경이 완료 되었습니다."));
    }
}
