package com.financetracker.recurring_payment_service.service;

import com.financetracker.recurring_payment_service.entity.RecurringPayment;
import com.financetracker.recurring_payment_service.repo.RecurringPaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringPaymentService {

    @Autowired
    private RecurringPaymentRepo recurringPaymentRepository;

    // Create a recurring payment
    public RecurringPayment createRecurringPayment(RecurringPayment recurringPayment) {
        recurringPayment.setCreatedAt(LocalDateTime.now());
        recurringPayment.setUpdatedAt(LocalDateTime.now());
        return recurringPaymentRepository.save(recurringPayment);
    }

    // Get a recurring payment by ID
    public Optional<RecurringPayment> getRecurringPaymentById(Long id) {
        return recurringPaymentRepository.findById(id);
    }

    // Get all recurring payments for a user
    public List<RecurringPayment> getRecurringPaymentsByUserId(Long userId) {
        return recurringPaymentRepository.findByUserId(userId);
    }

    // Update a recurring payment
    public RecurringPayment updateRecurringPayment(Long id, RecurringPayment updatedPayment) {
        return recurringPaymentRepository.findById(id).map(payment -> {
            payment.setCategory(updatedPayment.getCategory());
            payment.setAmount(updatedPayment.getAmount());
            payment.setCurrency(updatedPayment.getCurrency());
            payment.setFrequency(updatedPayment.getFrequency());
            payment.setStartDate(updatedPayment.getStartDate());
            payment.setEndDate(updatedPayment.getEndDate());
            payment.setDescription(updatedPayment.getDescription());
            payment.setIsActive(updatedPayment.getIsActive());
            payment.setUpdatedAt(LocalDateTime.now());
            return recurringPaymentRepository.save(payment);
        }).orElseThrow(() -> new RuntimeException("Recurring payment not found"));
    }

    // Delete a recurring payment by ID
    public void deleteRecurringPayment(Long id) {
        recurringPaymentRepository.deleteById(id);
    }

    // Deactivate a recurring payment
    public RecurringPayment deactivateRecurringPayment(Long id) {
        return recurringPaymentRepository.findById(id).map(payment -> {
            payment.setIsActive(false);
            payment.setUpdatedAt(LocalDateTime.now());
            return recurringPaymentRepository.save(payment);
        }).orElseThrow(() -> new RuntimeException("Recurring payment not found"));
    }
}
