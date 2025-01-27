package com.financetracker.recurring_payment_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {


    private Long id;

    private String fullName;

    private String email;

    private String password;

    private String preferredCurrency;
}
