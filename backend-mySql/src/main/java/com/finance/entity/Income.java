package com.finance.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "income")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String sourceName;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate dateReceived;

    @Column(nullable = false)
    private Integer month; // Derived field for filtering by month

    @Column(nullable = false)
    private Integer year; // Derived field for filtering by year

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column
    private LocalDate updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDate.now();
    }
}