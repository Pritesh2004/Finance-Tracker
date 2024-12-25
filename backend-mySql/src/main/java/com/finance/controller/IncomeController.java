package com.finance.controller;

import com.finance.entity.Income;
import com.finance.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping("/")
    public ResponseEntity<Income> createIncome(@PathVariable Long userId, @RequestBody Income income) {
        return ResponseEntity.ok(incomeService.createIncome(userId, income));
    }

    @GetMapping("/{incomeId}")
    public ResponseEntity<Income> getIncomeById(@PathVariable Long userId, @PathVariable Long incomeId) {
        Income income = incomeService.getIncomeById(userId, incomeId);
        return income != null ? ResponseEntity.ok(income) : ResponseEntity.notFound().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<Income>> getAllIncomes(@PathVariable Long userId) {
        return ResponseEntity.ok(incomeService.getAllIncomes(userId));
    }

    @PutMapping("/{incomeId}")
    public ResponseEntity<Income> updateIncome(@PathVariable Long userId, @PathVariable Long incomeId, @RequestBody Income income) {
        return ResponseEntity.ok(incomeService.updateIncome(userId, incomeId, income));
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long userId, @PathVariable Long incomeId) {
        incomeService.deleteIncome(userId, incomeId);
        return ResponseEntity.noContent().build();
    }
}
