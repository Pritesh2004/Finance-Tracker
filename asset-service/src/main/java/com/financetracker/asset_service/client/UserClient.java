package com.financetracker.asset_service.client;

import com.financetracker.asset_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// UserClient.java
@Component
@RequiredArgsConstructor
public class UserClient {

    private final WebClient.Builder webClientBuilder;

    public boolean validateUserExistence(Long userId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://USER-SERVICE/user/{id}", userId)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block(); // Blocking here for simplicity; consider non-blocking in real applications.
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        }
    }

}
