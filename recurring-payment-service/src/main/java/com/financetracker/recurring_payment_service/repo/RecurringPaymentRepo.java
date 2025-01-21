package com.financetracker.recurring_payment_service.repo;

import com.financetracker.recurring_payment_service.entity.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurringPaymentRepo extends JpaRepository<RecurringPayment, Long> {
    List<RecurringPayment> findByUserId(Long userId);

    List<RecurringPayment> findByFrequencyAndIsActive(String daily, boolean b);

    List<RecurringPayment> findByIsActive(boolean b);
}
