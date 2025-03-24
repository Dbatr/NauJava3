package ru.denis.NauJava3.dao;

import ru.denis.NauJava3.entity.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс для реализации пользовательских методов поиска счетов.
 * Предоставляет расширенные возможности поиска с использованием Criteria API.
 */
public interface AccountRepositoryCustom {
    /**
     * Поиск счетов пользователя по email и диапазону баланса с использованием Criteria API
     * @param userEmail email пользователя
     * @param minBalance минимальный баланс
     * @param maxBalance максимальный баланс
     * @return список найденных счетов
     */
    List<Account> findUserAccountsByEmailAndBalanceRangeCriteria(
            String userEmail,
            BigDecimal minBalance,
            BigDecimal maxBalance
    );
}
