package com.stock.entity;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @PropertyName("expenseId")
    private String expenseId;

    @PropertyName("category")
    private String category;

    @PropertyName("amount")
    private Double amount;

    @PropertyName("description")
    private String description;

    @PropertyName("dateSpent")
    private LocalDate dateSpent; // Use LocalDate for better date handling

    @PropertyName("month")
    private Integer month;

    @PropertyName("year")
    private Integer year;
}