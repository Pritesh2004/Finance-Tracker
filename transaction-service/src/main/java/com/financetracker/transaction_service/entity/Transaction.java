package com.financetracker.transaction_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId; // Foreign key to User table

    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME, EXPENSE, RECURRING, SAVINGS

    private String category; // e.g., "Rent", "Salary"

    private BigDecimal amount;

    private String currency; // e.g., "USD", "INR"

    private LocalDateTime transactionDate;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
