package com.financetracker.transaction_service.service;

import com.financetracker.transaction_service.entity.Transaction;
import com.financetracker.transaction_service.entity.TransactionType;
import com.financetracker.transaction_service.repo.TransactionRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TransactionService(TransactionRepo transactionRepository, WebClient.Builder webClientBuilder) {
        this.transactionRepository = transactionRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // Create a new transaction
    public Transaction createTransaction(Transaction transaction) {
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    // Get a transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    // Get all transactions for a user
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    // Get transactions by type for a user
    public List<Transaction> getTransactionsByType(Long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type);
    }

    // Update a transaction
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existingTransaction = getTransactionById(id);
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setCurrency(updatedTransaction.getCurrency());
        existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(existingTransaction);
    }

    // Delete a transaction
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public String getUserDetails(Long userId, String jwtToken) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/user/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken) // Include JWT token in the Authorization header
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public BigDecimal getTotalSpentByCategory(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndCategoryAndTransactionDateBetween(
                userId, category, startDate, endDate);

        // Sum the amount spent in the specified category and date range
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


