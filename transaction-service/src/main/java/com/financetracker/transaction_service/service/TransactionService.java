package com.financetracker.transaction_service.service;

import com.financetracker.transaction_service.client.AssetClient;
import com.financetracker.transaction_service.client.BudgetClient;
import com.financetracker.transaction_service.entity.Asset;
import com.financetracker.transaction_service.entity.Budget;
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
    private final AssetClient assetClient;
    private final BudgetClient budgetClient;

    @Autowired
    public TransactionService(TransactionRepo transactionRepository, WebClient.Builder webClientBuilder, AssetClient assetClient, BudgetClient budgetClient) {
        this.transactionRepository = transactionRepository;
        this.webClientBuilder = webClientBuilder;
        this.assetClient = assetClient;
        this.budgetClient = budgetClient;
    }

    // Create a new transaction
    public Transaction createTransaction(Transaction transaction) {

        // Handle Expense Transactions
        if (transaction.getType() == TransactionType.EXPENSE) {
            // 1. Update Asset Service - Subtract from Cash Asset
            updateCashAssetForExpense(transaction);

            // 2. Update Budget Service - Add spentAmount
            updateBudgetForExpense(transaction);
        }

        // Handle Income Transactions
        if (transaction.getType() == TransactionType.INCOME) {
            // 1. Update Asset Service - Add to Cash Asset
            updateCashAssetForIncome(transaction);
        }

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    // Helper method to update cash asset for expense transactions
    private void updateCashAssetForExpense(Transaction transaction) {
        // Fetch the Cash Asset from Asset Service
        Asset cashAsset = assetClient.getCashAssetByUserId(transaction.getUserId());

        if (cashAsset != null) {
            BigDecimal newCashValue = cashAsset.getCurrentValue().subtract(transaction.getAmount());
            cashAsset.setCurrentValue(newCashValue);
            assetClient.updateAsset(cashAsset);  // Update Asset Service
        } else {
            throw new RuntimeException("Cash asset not found for user ID: " + transaction.getUserId());
        }
    }

    // Helper method to update budget for expense transactions
    private void updateBudgetForExpense(Transaction transaction) {
        // Fetch the Budget for the user and category from Budget Service
        List<Budget> userBudgets = budgetClient.getBudgetsByUserIdAndCategory(transaction.getUserId(), transaction.getCategory());

        for (Budget budget : userBudgets) {
            if (transaction.getTransactionDate().isAfter(budget.getStartDate().atStartOfDay()) && transaction.getTransactionDate().isBefore(budget.getEndDate().atStartOfDay())) {
                BigDecimal newSpentAmount = budget.getSpentAmount().add(transaction.getAmount());

                // Check if the spent amount exceeds the allocated amount
                if (newSpentAmount.compareTo(budget.getAllocatedAmount()) > 0) {
                    throw new RuntimeException("Budget exceeded for category: " + transaction.getCategory());
                }
                budget.setSpentAmount(newSpentAmount);
                budgetClient.updateBudget(budget);  // Update Budget Service
            }
        }
    }

    // Helper method to update cash asset for income transactions
    private void updateCashAssetForIncome(Transaction transaction) {
        // Fetch the Cash Asset from Asset Service
        Asset cashAsset = assetClient.getCashAssetByUserId(transaction.getUserId());

        if (cashAsset != null) {
            BigDecimal newCashValue = cashAsset.getCurrentValue().add(transaction.getAmount());
            cashAsset.setCurrentValue(newCashValue);
            assetClient.updateAsset(cashAsset);  // Update Asset Service
        } else {
            throw new RuntimeException("Cash asset not found for user ID: " + transaction.getUserId());
        }
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


