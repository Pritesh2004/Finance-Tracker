package com.financetracker.budget_service.client;

import com.financetracker.budget_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// UserClient.java
@Component
@RequiredArgsConstructor
public class UserClient {

    private final WebClient.Builder webClientBuilder;


}
