package com.stock.entity;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @PropertyName("incomeId")
    private String incomeId;

    @PropertyName("sourceName")
    private String sourceName;

    @PropertyName("amount")
    private Double amount;

    @PropertyName("dateReceived")
    private LocalDate dateReceived; // Use LocalDate for better date handling

    @PropertyName("month")
    private Integer month;

    @PropertyName("year")
    private Integer year;
}