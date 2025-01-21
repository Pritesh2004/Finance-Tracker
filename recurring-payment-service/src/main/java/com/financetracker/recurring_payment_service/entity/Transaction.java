package com.financetracker.recurring_payment_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

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
