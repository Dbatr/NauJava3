package ru.denis.NauJava3.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.denis.NauJava3.dto.BudgetDto;
import ru.denis.NauJava3.entity.Budget;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.exception.ResourceNotFoundException;
import ru.denis.NauJava3.repository.BudgetRepository;
import ru.denis.NauJava3.repository.CategoryRepository;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.repository.UserRepository;
import ru.denis.NauJava3.service.BudgetService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Budget createBudget(BudgetDto budgetDto) {
        User currentUser = getCurrentUser();

        Category category = categoryRepository.findById(budgetDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с id: " + budgetDto.getCategoryId()));

        Budget budget = new Budget();
        budget.setName(budgetDto.getName());
        budget.setAmountLimit(budgetDto.getAmountLimit());
        budget.setPeriodStart(budgetDto.getPeriodStart());
        budget.setPeriodEnd(budgetDto.getPeriodEnd());
        budget.setUser(currentUser);
        budget.setCategory(category);

        return budgetRepository.save(budget);
    }

    @Override
    public Budget getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бюджет не найден с id: " + id));

        User currentUser = getCurrentUser();
        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Бюджет не найден с id: " + id);
        }

        return budget;
    }

    @Override
    public List<Budget> getCurrentUserBudgets() {
        User currentUser = getCurrentUser();
        return budgetRepository.findByUser(currentUser);
    }

    @Override
    public boolean isBudgetExceeded(Long budgetId) {
        Budget budget = getBudgetById(budgetId);
        BigDecimal spentAmount = calculateSpentAmount(budget);
        return spentAmount.compareTo(budget.getAmountLimit()) > 0;
    }

    private BigDecimal calculateSpentAmount(Budget budget) {
        LocalDateTime startDateTime = budget.getPeriodStart().atStartOfDay();
        LocalDateTime endDateTime = budget.getPeriodEnd().atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserAndCategoryAndDateBetween(
                budget.getUser(),
                budget.getCategory(),
                startDateTime,
                endDateTime
        );

        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получает текущего аутентифицированного пользователя из контекста безопасности
     * @return Объект текущего пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
    }
}
