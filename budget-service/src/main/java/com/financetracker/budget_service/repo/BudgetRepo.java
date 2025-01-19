package com.financetracker.budget_service.repo;

import com.financetracker.budget_service.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Long> {
}
