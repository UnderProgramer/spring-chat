package com.example.springChat.global.chat;

import com.example.springChat.global.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@AllArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if(StompCommand.CONNECT.equals(command)){
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Authorization에 토큰이 없습니다");
            }

            token = token.substring(7);

            if (!tokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("Jwt 토큰이 잘못 되었습니다.");
            }
            String username = tokenProvider.getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            accessor.setUser(authentication);
        }

        if (StompCommand.SEND.equals(command)) {
            Authentication user = (Authentication) accessor.getUser();
            if (user == null) {
                throw new AccessDeniedException("인증 되지 않았습니다.");
            }
        }
        return message;
    }
}
