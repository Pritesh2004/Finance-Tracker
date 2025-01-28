package com.financetracker.budget_service.service;

import com.financetracker.budget_service.client.TransactionClient;
import com.financetracker.budget_service.entity.Budget;
import com.financetracker.budget_service.entity.BudgetStatus;
import com.financetracker.budget_service.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepo budgetRepository;

    @Autowired
    private TransactionClient transactionClient;

    // Create a new budget
    public Budget createBudget(Budget budget) {
        budget.setStatus(BudgetStatus.ACTIVE);
        budget.setSpentAmount(BigDecimal.ZERO);
        return budgetRepository.save(budget);
    }

    // Get all budgets for a user
    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    // Get a single budget by ID
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public List<Budget> getBudgetsByUserIdAndCategory(Long userId, String category) {
        return budgetRepository.findByUserIdAndCategory(userId, category);
    }
    // Update a budget
    public Budget updateBudget(Long id, Budget updatedBudget) {
        return budgetRepository.findById(id).map(existingBudget -> {
            existingBudget.setCategory(updatedBudget.getCategory());
            existingBudget.setAllocatedAmount(updatedBudget.getAllocatedAmount());
            existingBudget.setStartDate(updatedBudget.getStartDate());
            existingBudget.setEndDate(updatedBudget.getEndDate());
            existingBudget.setStatus(updatedBudget.getStatus());
            return budgetRepository.save(existingBudget);
        }).orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    // Delete a budget
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    // Update spent amount dynamically
    public Mono<Budget> updateSpentAmount(Long budgetId) {
        return Mono.defer(() -> budgetRepository.findById(budgetId)
                        .map(budget -> {
                            return transactionClient.getTotalSpentByCategory(
                                            budget.getUserId(),
                                            budget.getCategory(),
                                            budget.getStartDate(),
                                            budget.getEndDate()
                                    )
                                    .map(spentAmount -> {
                                        budget.setSpentAmount(spentAmount);
                                        return budgetRepository.save(budget);
                                    });
                        })
                        .orElse(Mono.error(new RuntimeException("Budget not found"))))
                .map(mono -> mono);
    }
}
