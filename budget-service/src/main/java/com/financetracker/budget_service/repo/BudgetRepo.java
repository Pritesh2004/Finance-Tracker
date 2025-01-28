package com.financetracker.budget_service.repo;

import com.financetracker.budget_service.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    List<Budget> findByUserIdAndCategory(Long userId, String category);

}
