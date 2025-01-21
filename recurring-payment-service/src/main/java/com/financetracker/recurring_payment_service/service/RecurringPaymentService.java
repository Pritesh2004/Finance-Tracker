package com.financetracker.recurring_payment_service.service;

import com.financetracker.recurring_payment_service.client.TransactionClient;
import com.financetracker.recurring_payment_service.entity.RecurringPayment;
import com.financetracker.recurring_payment_service.entity.Transaction;
import com.financetracker.recurring_payment_service.repo.RecurringPaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringPaymentService {

    @Autowired
    private RecurringPaymentRepo recurringPaymentRepository;

    @Autowired
    private TransactionClient transactionClient;

    // Create a recurring payment
    public RecurringPayment createRecurringPayment(RecurringPayment recurringPayment) {
        recurringPayment.setCreatedAt(LocalDateTime.now());
        recurringPayment.setUpdatedAt(LocalDateTime.now());
        RecurringPayment savedPayment = recurringPaymentRepository.save(recurringPayment);

        // Create a transaction from the recurring payment
        createTransactionFromRecurringPayment(savedPayment);

        return savedPayment;
    }

    // Process recurring payments based on their frequency
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void processRecurringPayments() {
        List<RecurringPayment> recurringPayments = recurringPaymentRepository.findByIsActive(true);

        for (RecurringPayment recurringPayment : recurringPayments) {
            LocalDate today = LocalDate.now();

            // Check if the recurring payment is active and within its start and end date range
            if (isWithinDateRange(today, recurringPayment)) {

                // Process based on frequency
                switch (recurringPayment.getFrequency()) {
                    case DAILY:
                        if (recurringPayment.getStartDate().isBefore(today) || recurringPayment.getStartDate().isEqual(today)) {
                            createTransactionFromRecurringPayment(recurringPayment);
                        }
                        break;

                    case WEEKLY:
                        // Check if the payment is due this week (same weekday as start date)
                        if (isWeeklyPaymentDue(recurringPayment, today)) {
                            createTransactionFromRecurringPayment(recurringPayment);
                        }
                        break;

                    case MONTHLY:
                        // Check if the payment is due this month (same day as start date)
                        if (isMonthlyPaymentDue(recurringPayment, today)) {
                            createTransactionFromRecurringPayment(recurringPayment);
                        }
                        break;

                    case YEARLY:
                        // Check if the payment is due this year (same day and month as start date)
                        if (isYearlyPaymentDue(recurringPayment, today)) {
                            createTransactionFromRecurringPayment(recurringPayment);
                        }
                        break;
                }
            }
        }
    }

    // Create transaction from recurring payment
    private void createTransactionFromRecurringPayment(RecurringPayment recurringPayment) {
        Transaction transactionRequest = new Transaction();
        transactionRequest.setUserId(recurringPayment.getUserId());
        transactionRequest.setRecurringPaymentId(recurringPayment.getId());
        transactionRequest.setCategory(recurringPayment.getCategory());
        transactionRequest.setType(recurringPayment.getTransactionType());
        transactionRequest.setAmount(recurringPayment.getAmount());
        transactionRequest.setCurrency(recurringPayment.getCurrency());
        transactionRequest.setTransactionDate(LocalDateTime.now());
        transactionRequest.setDescription("Recurring payment for " + recurringPayment.getCategory());
        transactionRequest.setRecurring(true);  // Mark the transaction as recurring

        // Create the transaction and get the transaction ID
        Long transactionId = transactionClient.createTransactionAndGetId(transactionRequest);

        // Optionally handle the transaction ID here (e.g., log it, associate it with the recurring payment, etc.)
    }

    // Check if the current date is within the valid range for recurring payment
    private boolean isWithinDateRange(LocalDate today, RecurringPayment recurringPayment) {
        return (recurringPayment.getStartDate().isBefore(today) || recurringPayment.getStartDate().isEqual(today)) &&
                (recurringPayment.getEndDate() == null || recurringPayment.getEndDate().isAfter(today));
    }

    // Check if the weekly payment is due today (same weekday as the start date)
    private boolean isWeeklyPaymentDue(RecurringPayment recurringPayment, LocalDate today) {
        return recurringPayment.getStartDate().getDayOfWeek().equals(today.getDayOfWeek()) &&
                !recurringPayment.getStartDate().isAfter(today);
    }

    // Check if the monthly payment is due today (same day of the month as the start date)
    private boolean isMonthlyPaymentDue(RecurringPayment recurringPayment, LocalDate today) {
        return recurringPayment.getStartDate().getDayOfMonth() == today.getDayOfMonth() &&
                !recurringPayment.getStartDate().isAfter(today);
    }

    // Check if the yearly payment is due today (same day and month as the start date)
    private boolean isYearlyPaymentDue(RecurringPayment recurringPayment, LocalDate today) {
        return recurringPayment.getStartDate().getDayOfMonth() == today.getDayOfMonth() &&
                recurringPayment.getStartDate().getMonth() == today.getMonth() &&
                !recurringPayment.getStartDate().isAfter(today);
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
            payment.setTransactionType(updatedPayment.getTransactionType());
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
