package com.financetracker.transaction_service.client;

import com.financetracker.transaction_service.entity.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BudgetClient {

    private final WebClient webClient;

    @Autowired
    public BudgetClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://budget-service-url").build();
    }

    public List<Budget> getBudgetsByUserIdAndCategory(Long userId, String category) {
        return this.webClient.get()
            .uri("/budgets/user/{userId}/category/{category}", userId, category)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Budget>>() {})
            .block();
    }

    public void updateBudget(Budget budget) {
        this.webClient.put()
            .uri("/budgets/{id}", budget.getId())
            .bodyValue(budget)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
