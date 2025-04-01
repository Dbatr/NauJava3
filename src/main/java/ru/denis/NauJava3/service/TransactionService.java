package ru.denis.NauJava3.service;

import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.enums.OperationType;
import java.math.BigDecimal;

/**
 * Сервис для управления финансовыми транзакциями.
 */
public interface TransactionService {
    /**
     * Создает транзакцию и обновляет баланс счета
     * @param accountId ID счета
     * @param categoryId ID категории
     * @param amount сумма транзакции
     * @param description описание транзакции
     * @param type тип транзакции (доход/расход)
     * @return созданная транзакция
     */
    Transaction createTransaction(
            Long accountId,
            Long categoryId,
            BigDecimal amount,
            String description,
            OperationType type
    );

    /**
     * Удаляет транзакцию и восстанавливает баланс счета
     * @param transactionId ID транзакции
     */
    void deleteTransaction(Long transactionId);
}
