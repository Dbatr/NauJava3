package ru.denis.NauJava3.factory;

import org.springframework.stereotype.Component;
import ru.denis.NauJava3.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Фабрика для создания транзакций
 */
@Component
public class TransactionFactory {

    /**
     * Создает новую транзакцию
     * @param amount сумма
     * @param category категория
     * @param description описание
     * @param type тип транзакции
     * @return новая транзакция
     */
    public Transaction createTransaction(BigDecimal amount, String category, String description, Transaction.TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setType(type);
        transaction.setDate(LocalDateTime.now());
        return transaction;
    }

    /**
     * Создает транзакцию дохода
     */
    public Transaction createIncome(BigDecimal amount, String category, String description) {
        return createTransaction(amount, category, description, Transaction.TransactionType.INCOME);
    }

    /**
     * Создает транзакцию расхода
     */
    public Transaction createExpense(BigDecimal amount, String category, String description) {
        return createTransaction(amount, category, description, Transaction.TransactionType.EXPENSE);
    }
}
