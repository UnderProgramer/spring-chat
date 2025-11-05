package com.example.springChat.service;

import com.example.springChat.Enum.Role;
import com.example.springChat.dto.auth.LoginRequestDTO;
import com.example.springChat.dto.auth.LoginResponseDTO;
import com.example.springChat.dto.auth.RegisterRequestDTO;
import com.example.springChat.entity.Users;
import com.example.springChat.exception.AlreadyExistsUserEmail;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.global.TokenProvider;
import com.example.springChat.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public void registerService(RegisterRequestDTO dto) {
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new AlreadyExistsUserEmail("이미 가입된 이메일 입니다.");
        }

        Users user = Users.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password_hash(passwordEncoder.encode(dto.getPassword()))
                .role(Role.GUEST)
                .build();

        userRepository.save(user);
    }

    public LoginResponseDTO loginService(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        Users user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new UserNotFound("사용자를 찾는 과정에서 오류가 났습니다."));
        String accessToken = tokenProvider.createAccessToken(dto.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(dto.getEmail());

        return LoginResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
