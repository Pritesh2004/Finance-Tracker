package com.financetracker.asset_service.client;

import com.financetracker.asset_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.springframework.http.HttpHeaders;

// UserClient.java
@Component
public class UserClient {

    private final WebClient webClient;

    @Autowired
    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public boolean validateUserExistence(Long userId, String jwtToken) {
        try {
            User user = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}")
                            .build(userId)) // Safer URL construction
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block(); // Consider replacing block() with a reactive approach if possible

            return user != null;

        } catch (WebClientResponseException e) {
            // Handle the case where the user does not exist (e.g., 404 Not Found)
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            // Handle other errors (e.g., network issues)
            throw new RuntimeException("Error occurred while validating user existence", e);
        } catch (Exception e) {
            // Handle other general errors
            throw new RuntimeException("Error occurred while validating user existence", e);
        }
    }


}
