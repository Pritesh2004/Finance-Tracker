package com.financetracker.transaction_service.controller;

import com.financetracker.transaction_service.entity.Transaction;
import com.financetracker.transaction_service.entity.TransactionType;
import com.financetracker.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
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

    // Create a new transaction
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction, @RequestHeader("Authorization") String authorizationHeader ) {
        System.out.println("Header - "+authorizationHeader);
        Transaction createdTransaction = transactionService.createTransaction(transaction, authorizationHeader);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // Get a transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // Get transactions by date range and type
    @GetMapping("/range/type")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRangeAndType(
            @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String type) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRangeAndType(userId, startDate, endDate, type);
        return ResponseEntity.ok(transactions);
    }

    // Get transactions by date range and category
    @GetMapping("/range/category")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRangeAndCategory(
            @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String category) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRangeAndCategory(userId, startDate, endDate, category);
        return ResponseEntity.ok(transactions);
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

