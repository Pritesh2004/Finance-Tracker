package com.financetracker.transaction_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private boolean isRecurring;

    private Long recurringPaymentId; // To associate with RecurringPayment entity

    private String category;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime transactionDate;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
