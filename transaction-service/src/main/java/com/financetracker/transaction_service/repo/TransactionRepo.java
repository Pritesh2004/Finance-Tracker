package com.financetracker.transaction_service.repo;

import com.financetracker.transaction_service.entity.Transaction;
import com.financetracker.transaction_service.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndCategoryAndTransactionDateBetween(Long userId, String category, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserIdAndTransactionDateBetweenAndType(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, TransactionType transactionType);

    List<Transaction> findByUserIdAndTransactionDateBetweenAndCategory(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, String category);
}
