package com.finance.service;

import com.finance.entity.Expense;
import com.finance.entity.User;
import com.finance.repository.ExpenseRepository;
import com.finance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public Expense createExpense(Long userId, Expense expense) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        expense.setExpenseId(null);// Ensure new expense is created
        expense.setUser(user);
        expenseRepository.save(expense);
        user.getExpenses().put(expense.getExpenseId(), expense);
        userRepository.save(user);
        return expense;
    }

    public Expense getExpenseById(Long userId, Long expenseId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getExpenses().containsKey(expenseId)) {
            return user.getExpenses().get(expenseId);
        }
        return null;
    }

    public List<Expense> getAllExpenses(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? List.copyOf(user.getExpenses().values()) : List.of();
    }

    public Expense updateExpense(Long userId, Long expenseId, Expense expense) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getExpenses().containsKey(expenseId)) {
            expense.setExpenseId(expenseId);
            expenseRepository.save(expense);
            user.getExpenses().put(expenseId, expense);
            userRepository.save(user);
            return expense;
        }
        throw new RuntimeException("Expense not found for user");
    }

    public void deleteExpense(Long userId, Long expenseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getExpenses().containsKey(expenseId)) {
            user.getExpenses().remove(expenseId);
            expenseRepository.deleteById(expenseId);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Expense not found for user");
        }
    }
}
