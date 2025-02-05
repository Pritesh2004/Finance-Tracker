package com.financetracker.transaction_service.service;

import com.financetracker.transaction_service.client.AssetClient;
import com.financetracker.transaction_service.client.BudgetClient;
import com.financetracker.transaction_service.client.NotificationClient;
import com.financetracker.transaction_service.client.UserClient;
import com.financetracker.transaction_service.entity.*;
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
    private final AssetClient assetClient;
    private final BudgetClient budgetClient;
    private final UserClient userClient;
    private final NotificationClient notificationClient;
    @Autowired
    public TransactionService(TransactionRepo transactionRepository, AssetClient assetClient, BudgetClient budgetClient, UserClient userClient, NotificationClient notificationClient) {
        this.transactionRepository = transactionRepository;
        this.assetClient = assetClient;
        this.budgetClient = budgetClient;
        this.notificationClient = notificationClient;
        this.userClient = userClient;
    }

    // Create a new transaction
    public Transaction createTransaction(Transaction transaction, String token) {

        // Handle Expense Transactions
        if (transaction.getType() == TransactionType.EXPENSE) {
            // 1. Update Asset Service - Subtract from Cash Asset
            updateCashAssetForExpense(transaction);

            // 2. Update Budget Service - Add spentAmount
            updateBudgetForExpense(transaction, token);
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
        Asset cashAsset = assetClient.getCashAssetByUserId(transaction.getUserId(), "Cash");

        if (cashAsset != null) {
            if(transaction.getAmount().compareTo(cashAsset.getCurrentValue())>0){
                throw new RuntimeException("Low Cash asset: " + transaction.getUserId());
            }
            BigDecimal newCashValue = cashAsset.getCurrentValue().subtract(transaction.getAmount());
            cashAsset.setCurrentValue(newCashValue);
            assetClient.updateAsset(cashAsset);  // Update Asset Service
        } else {
            throw new RuntimeException("Cash asset not found for user ID: " + transaction.getUserId());
        }
    }

    // Helper method to update budget for expense transactions
    private void updateBudgetForExpense(Transaction transaction, String token) {
        // Fetch the Budget for the user and category from Budget Service
        List<Budget> userBudgets = budgetClient.getBudgetsByUserIdAndCategory(transaction.getUserId(), transaction.getCategory());

        for (Budget budget : userBudgets) {
            if (transaction.getTransactionDate().isAfter(budget.getStartDate().atStartOfDay()) && transaction.getTransactionDate().isBefore(budget.getEndDate().atStartOfDay())) {
                BigDecimal newSpentAmount = budget.getSpentAmount().add(transaction.getAmount());

                // Check if the spent amount exceeds the allocated amount
                if (newSpentAmount.compareTo(budget.getAllocatedAmount()) > 0) {
                    User user = userClient.getUserById(transaction.getUserId(), token);
                    notificationClient.sendNotification(user);
                }
                budget.setSpentAmount(newSpentAmount);
                budgetClient.updateBudget(budget);  // Update Budget Service
            }
        }
    }

    // Helper method to update cash asset for income transactions
    private void updateCashAssetForIncome(Transaction transaction) {
        // Fetch the Cash Asset from Asset Service
        Asset cashAsset = assetClient.getCashAssetByUserId(transaction.getUserId(),"Cash");

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


    // Get transactions by date range and type
    public List<Transaction> getTransactionsByDateRangeAndType(Long userId, LocalDate startDate, LocalDate endDate, String type) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return transactionRepository.findByUserIdAndTransactionDateBetweenAndType(
                userId, startDateTime, endDateTime, TransactionType.valueOf(type.toUpperCase()));
    }

    // Get transactions by date range and category
    public List<Transaction> getTransactionsByDateRangeAndCategory(Long userId, LocalDate startDate, LocalDate endDate, String category) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return transactionRepository.findByUserIdAndTransactionDateBetweenAndCategory(
                userId, startDateTime, endDateTime, category);
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


    public BigDecimal getTotalSpentByCategory(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndCategoryAndTransactionDateBetween(
                userId, category, startDate, endDate);

        // Sum the amount spent in the specified category and date range
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


