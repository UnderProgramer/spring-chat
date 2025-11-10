package com.example.springChat.global;

import com.example.springChat.entity.Users;
import com.example.springChat.exception.UserNotFound;
import com.example.springChat.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository authRepository){
        this.userRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {
        Users user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFound(("사용자를 찾을 수 없습니다: " + username)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword_hash())
                .build();
    }
}
