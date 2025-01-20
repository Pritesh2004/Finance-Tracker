package com.financetracker.recurring_payment_service.controller;

import com.financetracker.recurring_payment_service.entity.RecurringPayment;
import com.financetracker.recurring_payment_service.service.RecurringPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-payments")
public class RecurringPaymentController {

    @Autowired
    private RecurringPaymentService recurringPaymentService;

    // Create a recurring payment
    @PostMapping
    public ResponseEntity<RecurringPayment> createRecurringPayment(@RequestBody RecurringPayment recurringPayment) {
        RecurringPayment savedPayment = recurringPaymentService.createRecurringPayment(recurringPayment);
        return ResponseEntity.ok(savedPayment);
    }

    // Get a recurring payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<RecurringPayment> getRecurringPaymentById(@PathVariable Long id) {
        return recurringPaymentService.getRecurringPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all recurring payments for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecurringPayment>> getRecurringPaymentsByUserId(@PathVariable Long userId) {
        List<RecurringPayment> payments = recurringPaymentService.getRecurringPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    // Update a recurring payment
    @PutMapping("/{id}")
    public ResponseEntity<RecurringPayment> updateRecurringPayment(@PathVariable Long id, @RequestBody RecurringPayment updatedPayment) {
        try {
            RecurringPayment payment = recurringPaymentService.updateRecurringPayment(id, updatedPayment);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a recurring payment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringPayment(@PathVariable Long id) {
        recurringPaymentService.deleteRecurringPayment(id);
        return ResponseEntity.noContent().build();
    }

    // Deactivate a recurring payment
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<RecurringPayment> deactivateRecurringPayment(@PathVariable Long id) {
        try {
            RecurringPayment payment = recurringPaymentService.deactivateRecurringPayment(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
