package ru.denis.NauJava3.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.denis.NauJava3.dto.BudgetDto;
import ru.denis.NauJava3.entity.Budget;
import ru.denis.NauJava3.service.BudgetService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody BudgetDto budgetDto) {
        Budget createdBudget = budgetService.createBudget(budgetDto);
        return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @GetMapping
    public ResponseEntity<List<Budget>> getCurrentUserBudgets() {
        List<Budget> budgets = budgetService.getCurrentUserBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getBudgetStatus(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        boolean isExceeded = budgetService.isBudgetExceeded(id);

        Map<String, Object> result = Map.of(
                "budget", budget,
                "isExceeded", isExceeded
        );

        return ResponseEntity.ok(result);
    }
}