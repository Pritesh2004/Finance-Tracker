package com.financetracker.budget_service.controller;

import com.financetracker.budget_service.client.UserClient;
import com.financetracker.budget_service.entity.Budget;
import com.financetracker.budget_service.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserClient userClient;

    // Create a new budget
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        // Validate User Existence
        if (!userClient.validateUserExistence(budget.getUserId())) {
            throw new RuntimeException("User not found with ID: " + budget.getUserId());
        }
        Budget savedBudget = budgetService.createBudget(budget);
        return ResponseEntity.ok(savedBudget);
    }

    // Get all budgets for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable String userId) {
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    // Get a single budget by ID
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return budgetService.getBudgetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<Budget>> getBudgetsByUserIdAndCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        List<Budget> budgets = budgetService.getBudgetsByUserIdAndCategory(userId, category);
        if (budgets.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(budgets);
    }

    // Update a budget
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget updatedBudget) {
        try {
            Budget updated = budgetService.updateBudget(id, updatedBudget);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a budget
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }


    // Update spent amount dynamically
    @PatchMapping("/{id}/update-spent")
    public Mono<ResponseEntity<Budget>> updateSpentAmount(@PathVariable Long id) {
        return budgetService.updateSpentAmount(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
