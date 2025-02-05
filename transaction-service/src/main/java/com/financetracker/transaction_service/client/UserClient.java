package com.financetracker.transaction_service.client;

import com.financetracker.transaction_service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;

@Component
public class UserClient {

    private final WebClient webClient;

    @Autowired
    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-service").build();
    }

    public User getUserById(Long id, String token) {
        System.out.println("Token - "+token);
        return webClient.get()
                .uri("/api/users/{id}", id)  // Adjust the base URL accordingly
                .headers(headers -> headers.setBearerAuth(token.substring(7))) // Include JWT token
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Unauthorized or User Not Found"))
                )
                .bodyToMono(User.class)
                .block(); // Blocking for simplicity, but use reactive handling if possible
    }


}

