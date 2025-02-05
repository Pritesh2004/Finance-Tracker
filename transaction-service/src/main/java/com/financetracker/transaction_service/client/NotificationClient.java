package com.financetracker.transaction_service.client;

import com.financetracker.transaction_service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private final WebClient webClient;

    @Autowired
    public NotificationClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://notification-service").build();
    }

    public void sendNotification(User user){
        Long userId = user.getId();
        String email = user.getEmail();
        String message = "Hello " + user.getFullName() + "!Your budget limit exceeded.";
        String notificationType = "BUDGET_EXCEEDED";

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
