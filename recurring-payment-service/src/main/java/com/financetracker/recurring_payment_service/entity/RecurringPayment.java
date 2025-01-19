package com.financetracker.recurring_payment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecurringPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String category; // e.g., "Netflix Subscription"

    private BigDecimal amount;

    private String currency; // e.g., "USD", "INR"

    @Enumerated(EnumType.STRING)
    private Frequency frequency; // DAILY, WEEKLY, MONTHLY, YEARLY

    private LocalDate startDate;

    private LocalDate endDate; // Nullable for indefinite recurrence

    private String description;

    private Boolean isActive = true;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
}
