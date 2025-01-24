package com.financetracker.asset_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // The user to whom the asset belongs

    private String type;  // Type of asset (e.g., "Cash", "Stocks", "Real Estate")

    private BigDecimal value;  // The current value of the asset

    private String currency;  // e.g., "USD", "INR"

    private LocalDate acquiredDate;  // Date when the asset was acquired

    private String description;  // Description of the asset (e.g., "My first house")

    private Boolean isActive = true;  // Whether the asset is still active

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
