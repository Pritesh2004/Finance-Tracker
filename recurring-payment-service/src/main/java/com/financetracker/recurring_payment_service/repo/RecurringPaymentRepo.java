package com.financetracker.recurring_payment_service.repo;

import com.financetracker.recurring_payment_service.entity.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringPaymentRepo extends JpaRepository<RecurringPayment, Long> {
}
