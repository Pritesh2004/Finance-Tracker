package com.finance.controller;

import com.finance.entity.Expense;
import com.finance.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/")
    public ResponseEntity<Expense> createExpense(@PathVariable Long userId, @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.createExpense(userId, expense));
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long userId, @PathVariable Long expenseId) {
        Expense expense = expenseService.getExpenseById(userId, expenseId);
        return expense != null ? ResponseEntity.ok(expense) : ResponseEntity.notFound().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getAllExpenses(userId));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long userId, @PathVariable Long expenseId, @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.updateExpense(userId, expenseId, expense));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long userId, @PathVariable Long expenseId) {
        expenseService.deleteExpense(userId, expenseId);
        return ResponseEntity.noContent().build();
    }
}