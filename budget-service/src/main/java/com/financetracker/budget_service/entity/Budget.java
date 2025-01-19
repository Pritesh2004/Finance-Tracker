package com.financetracker.budget_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;  // The user to whom this budget belongs

    private String category;  // E.g., "Groceries", "Entertainment", etc.

    private BigDecimal allocatedAmount;  // The amount set for this budget category

    private BigDecimal spentAmount;  // The amount spent so far in this category (to be calculated via Transaction Service)

    private LocalDate startDate;  // The start date of the budget period

    private LocalDate endDate;  // The end date of the budget period

    @Enumerated(EnumType.STRING)
    private BudgetStatus status;  // Status of the budget (e.g., ACTIVE, CLOSED)

    // Getters and setters
}
