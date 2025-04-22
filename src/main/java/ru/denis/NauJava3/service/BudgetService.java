package ru.denis.NauJava3.service;

import ru.denis.NauJava3.dto.BudgetDto;
import ru.denis.NauJava3.entity.Budget;

import java.util.List;

public interface BudgetService {
    /**
     * Создает новый бюджет для текущего авторизованного пользователя
     * @param budgetDto DTO с данными для создания бюджета
     * @return Созданный бюджет
     */
    Budget createBudget(BudgetDto budgetDto);

    /**
     * Находит бюджет по его ID
     * @param id ID бюджета
     * @return Бюджет, если найден
     * @throws ru.denis.NauJava3.exception.ResourceNotFoundException если бюджет не найден
     */
    Budget getBudgetById(Long id);

    /**
     * Получает все бюджеты текущего авторизованного пользователя
     * @return Список бюджетов
     */
    List<Budget> getCurrentUserBudgets();

    /**
     * Проверяет, был ли превышен бюджет
     * @param budgetId ID бюджета
     * @return true, если бюджет превышен, false в противном случае
     * @throws ru.denis.NauJava3.exception.ResourceNotFoundException если бюджет не найден
     */
    boolean isBudgetExceeded(Long budgetId);
}
