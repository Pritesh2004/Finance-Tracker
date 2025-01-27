package com.financetracker.transaction_service.controller;

import com.financetracker.transaction_service.client.UserClient;
import com.financetracker.transaction_service.entity.Transaction;
import com.financetracker.transaction_service.entity.TransactionType;
import com.financetracker.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserClient userClient;

    // Create a new transaction
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        if (!userClient.validateUserExistence(transaction.getUserId())) {
            throw new RuntimeException("User not found with ID: " + transaction.getUserId());
        }
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // Get a transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // Get all transactions for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Get transactions by type for a user
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(
            @PathVariable Long userId,
            @PathVariable TransactionType type
    ) {
        List<Transaction> transactions = transactionService.getTransactionsByType(userId, type);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Update a transaction
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction updatedTransaction
    ) {
        Transaction updated = transactionService.updateTransaction(id, updatedTransaction);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Delete a transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user-details/{userId}")
    public ResponseEntity<String> getUserDetails(
            @PathVariable Long userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        // Extract the JWT token
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        String userDetails = transactionService.getUserDetails(userId, jwtToken);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @GetMapping("/spent")
    public Mono<BigDecimal> getTotalSpentByCategory(
            @RequestParam Long userId,
            @RequestParam String category,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        BigDecimal totalSpent = transactionService.getTotalSpentByCategory(userId, category, startDate, endDate);
        return Mono.just(totalSpent);
    }

}

