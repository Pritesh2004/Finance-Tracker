package com.financetracker.recurring_payment_service.client;

import com.financetracker.recurring_payment_service.entity.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionClient {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(TransactionClient.class);

    public TransactionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://transaction-service").build(); // Ensure the correct host and port
    }

    public Long createTransactionAndGetId(Transaction transaction) {
        try {
            return webClient
                    .post()
                    .uri("/api/transactions")
                    .bodyValue(transaction)
                    .retrieve()
                    .bodyToMono(Transaction.class) // Ensure it matches the expected response type
                    .map(Transaction::getId) // Extract the ID from the response
                    .block(); // Blocking for synchronous response
        } catch (Exception e) {
            logger.error("Error while creating transaction: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create transaction", e);
        }
    }
}
