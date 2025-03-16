package ru.denis.NauJava3.service;

import java.math.BigDecimal;

/**
 * Сервис для управления финансовыми операциями
 */
public interface TransactionService {
    /**
     * Добавляет новый доход
     * @param amount сумма дохода
     * @param category категория дохода
     * @param description описание дохода
     */
    void addIncome(BigDecimal amount, String category, String description);

    /**
     * Добавляет новый расход
     * @param amount сумма расхода
     * @param category категория расхода
     * @param description описание расхода
     */
    void addExpense(BigDecimal amount, String category, String description);

    /**
     * @return общая сумма всех доходов
     */
    BigDecimal getTotalIncome();

    /**
     * @return общая сумма всех расходов
     */
    BigDecimal getTotalExpense();
}
