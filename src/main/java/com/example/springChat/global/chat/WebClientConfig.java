package com.example.springChat.global.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient reportsWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:9999/")
                .build();
    }

}
