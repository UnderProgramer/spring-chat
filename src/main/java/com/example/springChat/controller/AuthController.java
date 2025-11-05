package com.example.springChat.controller;

import com.example.springChat.dto.ApiResponse;
import com.example.springChat.dto.auth.LoginRequestDTO;
import com.example.springChat.dto.auth.LoginResponseDTO;
import com.example.springChat.dto.auth.RegisterRequestDTO;
import com.example.springChat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @PostMapping
}
