package com.finance.service;


import com.finance.entity.Income;
import com.finance.entity.User;
import com.finance.repository.IncomeRepository;
import com.finance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    @Autowired
    public IncomeService(IncomeRepository incomeRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    public Income createIncome(Long userId, Income income) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        income.setIncomeId(null); // Ensure new income is created
        income.setUser(user);
        incomeRepository.save(income);
        user.getIncomes().put(income.getIncomeId(), income);
        userRepository.save(user);
        return income;
    }

    public Income getIncomeById(Long userId, Long incomeId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getIncomes().containsKey(incomeId)) {
            return user.getIncomes().get(incomeId);
        }
        return null;
    }

    public List<Income> getAllIncomes(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? List.copyOf(user.getIncomes().values()) : List.of();
    }

    public Income updateIncome(Long userId, Long incomeId, Income income) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getIncomes().containsKey(incomeId)) {
            income.setIncomeId(incomeId);
            incomeRepository.save(income);
            user.getIncomes().put(incomeId, income);
            userRepository.save(user);
            return income;
        }
        throw new RuntimeException("Income not found for user");
    }

    public void deleteIncome(Long userId, Long incomeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getIncomes().containsKey(incomeId)) {
            user.getIncomes().remove(incomeId);
            incomeRepository.deleteById(incomeId);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Income not found for user");
        }
    }
}
