package com.financetracker.transaction_service.entity;

import java.math.BigDecimal;
import java.util.List;

public class TransactionSummaryDTO {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netSavings;
    private List<Transaction> recentTransactions;

    // Constructor, getters, setters
}
