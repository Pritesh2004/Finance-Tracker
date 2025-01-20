package com.financetracker.budget_service.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransactionClient {

    private final WebClient webClient;

    // Constructor injection for WebClient
    public TransactionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://transaction-service").build();
    }

    /**
     * Fetches the total spent amount for a category.
     *
     * @param userId    the ID of the user
     * @param category  the category for which spent amount is calculated
     * @param startDate the start date of the budget period
     * @param endDate   the end date of the budget period
     * @return a Mono containing the total spent amount
     */
    public Mono<BigDecimal> getTotalSpentByCategory(String userId, String category, LocalDate startDate, LocalDate endDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/transactions/spent")
                        .queryParam("userId", userId)
                        .queryParam("category", category)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .build())
                .retrieve()
                .bodyToMono(BigDecimal.class);
    }
}
