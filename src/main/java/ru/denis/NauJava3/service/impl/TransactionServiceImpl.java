package ru.denis.NauJava3.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.denis.NauJava3.entity.Account;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.enums.OperationType;
import ru.denis.NauJava3.repository.AccountRepository;
import ru.denis.NauJava3.repository.CategoryRepository;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Реализация сервиса управления транзакциями.
 * Обеспечивает атомарность операций с транзакциями и связанными счетами
 * с использованием программного управления транзакциями.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    /** Менеджер транзакций для обеспечения атомарности операций */
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public TransactionServiceImpl(
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            PlatformTransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * {@inheritDoc}
     *
     * Метод выполняет следующие шаги:
     * 1. Проверяет существование счета и категории
     * 2. Проверяет соответствие типа операции категории
     * 3. Проверяет достаточность средств при расходной операции
     * 4. Создает новую транзакцию
     * 5. Обновляет баланс счета
     * 6. Сохраняет изменения в базе данных
     */
    @Override
    public Transaction createTransaction(
            Long accountId,
            Long categoryId,
            BigDecimal amount,
            String description,
            OperationType type) {

        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Transaction savedTransaction = null;

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Счет не найден"));

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));

            if (category.getType() != type) {
                throw new RuntimeException("Тип операции не соответствует категории");
            }

            if (type == OperationType.EXPENSE) {
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Недостаточно средств на счете");
                }
            }

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setCategory(category);
            transaction.setUser(account.getUser());
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transaction.setDate(LocalDateTime.now());
            transaction.setType(type);

            if (type == OperationType.INCOME) {
                account.setBalance(account.getBalance().add(amount));
            } else {
                account.setBalance(account.getBalance().subtract(amount));
            }

            savedTransaction = transactionRepository.save(transaction);
            accountRepository.save(account);

            transactionManager.commit(status);

            return savedTransaction;

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new RuntimeException("Ошибка при создании транзакции: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Метод выполняет следующие шаги:
     * 1. Находит транзакцию по ID
     * 2. Получает связанный счет
     * 3. Восстанавливает баланс счета (отменяет влияние удаляемой транзакции)
     * 4. Удаляет транзакцию
     * 5. Сохраняет обновленный баланс счета
     */
    @Override
    public void deleteTransaction(Long transactionId) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Транзакция не найдена"));

            Account account = transaction.getAccount();

            if (transaction.getType() == OperationType.INCOME) {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            } else {
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            }

            transactionRepository.delete(transaction);
            accountRepository.save(account);

            transactionManager.commit(status);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new RuntimeException("Ошибка при удалении транзакции: " + e.getMessage());
        }
    }
}