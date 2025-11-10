package com.example.springChat.service;

import com.example.springChat.Enum.Role;
import com.example.springChat.dto.auth.token.RefreshTokenRequestDTO;
import com.example.springChat.dto.auth.token.RefreshTokenResponseDTO;
import com.example.springChat.dto.auth.user.*;
import com.example.springChat.entity.RefreshToken;
import com.example.springChat.entity.Users;
import com.example.springChat.exception.AlreadyExistsUsername;
import com.example.springChat.exception.RefreshTokenExpiredException;
import com.example.springChat.exception.TokenNotValidatedException;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.global.TokenProvider;
import com.example.springChat.repository.RefreshTokenRepository;
import com.example.springChat.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public void registerService(RegisterRequestDTO dto) {
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new AlreadyExistsUsername("이미 가입된 사용자입니다.");
        }

        Users user = Users.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.GUEST)
                .build();

        userRepository.save(user);
    }

    public LoginResponseDTO loginService(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        Users user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new UserNotFound("사용자를 찾는 과정에서 오류가 났습니다."));
        String accessToken = tokenProvider.createAccessToken(dto.getUsername());
        String refreshToken = tokenProvider.createRefreshToken(dto.getUsername());

        RefreshToken refreshtoken = RefreshToken.builder()
                .refreshToken(refreshToken)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshtoken);

        return LoginResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public GetUserDTO getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자를 찾을 수 없습니다."));

        return GetUserDTO.builder()
                .username(username)
                .email(user.getEmail())
                .build();
    }

    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO dto) {
        if(!tokenProvider.validateToken(dto.getRefreshToken())){
            throw new TokenNotValidatedException("토큰 검증에 실패 했습니다.");
        }

        RefreshToken existingToken = refreshTokenRepository.findByRefreshToken(dto.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenExpiredException("토큰을 찾을 수 없습니다"));

        if(existingToken.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new RefreshTokenExpiredException("토큰이 만료 되었습니다");
        }

        String username = tokenProvider.getUsername(existingToken.getRefreshToken());
        String newAccessToken = tokenProvider.createAccessToken(username);

        return RefreshTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Transactional
    public void patchPassword(PatchPasswordDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자를 찾을 수 없습니다."));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, dto.getOriginPassword())
        );
        user.setPassword(passwordEncoder.encode(dto.getPatchPassword()));

        userRepository.save(user);
    }

    @Transactional
    public RefreshTokenResponseDTO patchUser(PatchUserDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("사용자를 찾을 수 없습니다."));

        user.setUsername(dto.getPatchUsername());
        user.setEmail(dto.getPatchEmail());

        userRepository.save(user);

        Users tokenUser = userRepository.findByUsername(dto.getPatchUsername())
                .orElseThrow(() -> new UserNotFound("username이 잘못 되었습니다."));

        String accessToken = tokenProvider.createAccessToken(tokenUser.getUsername());
        return RefreshTokenResponseDTO.builder()
                .accessToken(accessToken)
                .build();


    }
}
