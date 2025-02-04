package com.financetracker.userservice.client;

import com.financetracker.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class NotificationClient {

    private final WebClient webClient;

    @Autowired
    public NotificationClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public void sendUserCreatedNotification(User user){
        Long userId = user.getId();
        String email = user.getEmail();
        String message = "Welcome " + user.getFullName() + "! Your account has been created.";
        String notificationType = "ACCOUNT_CREATED";

        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/notifications/send")
                        .queryParam("userId", userId)
                        .queryParam("email", email)
                        .queryParam("message", message)
                        .queryParam("notificationType", notificationType)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .block();



    }
}