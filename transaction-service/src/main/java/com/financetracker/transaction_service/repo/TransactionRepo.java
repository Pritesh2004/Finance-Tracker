package com.financetracker.transaction_service.repo;

import com.financetracker.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
}
