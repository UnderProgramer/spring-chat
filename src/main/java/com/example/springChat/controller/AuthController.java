package com.example.springChat.controller;

import com.example.springChat.dto.ApiResponse;
import com.example.springChat.dto.auth.token.RefreshTokenRequestDTO;
import com.example.springChat.dto.auth.token.RefreshTokenResponseDTO;
import com.example.springChat.dto.auth.user.*;
import com.example.springChat.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequestDTO dto) {
        userService.registerService(dto);
        return ResponseEntity.ok(ApiResponse.ok("회원 가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginResponseDTO result =  userService.loginService(dto);
        return ResponseEntity.ok(ApiResponse.ok(result,"로그인 성공"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> refresh(@RequestBody RefreshTokenRequestDTO dto) {
        RefreshTokenResponseDTO result = userService.refresh(dto);
        return ResponseEntity.ok(ApiResponse.ok(result, "토큰 재발급 성공"));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody PatchPasswordDTO dto,
                                                            @AuthenticationPrincipal UserDetails userDetails){
        userService.patchPassword(dto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("비밀번호변경 성공"));
    }

    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> changeUserInfo(@RequestBody PatchUserDTO dto,
                                                                               @AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증 필요"));
        }
        RefreshTokenResponseDTO result = userService.patchUser(dto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(result, "사용자 정보 변경이 완료 되었습니다."));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<GetUserDTO>> getAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증 필요"));
        }
        GetUserDTO user = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(user, "계정 정보 조회 성공"));
    }
}
