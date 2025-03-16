package ru.denis.NauJava3.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.factory.TransactionFactory;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.service.TransactionService;

import java.math.BigDecimal;

/**
 * Реализация сервиса управления финансами
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;

    /**
     * @param transactionRepository репозиторий для работы с транзакциями
     * @param transactionFactory фабрика для создания транзакций
     */
    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionFactory transactionFactory) {
        this.transactionRepository = transactionRepository;
        this.transactionFactory = transactionFactory;
    }


    @Override
    public void addIncome(BigDecimal amount, String category, String description) {
        Transaction transaction = transactionFactory.createIncome(amount, category, description);
        transactionRepository.create(transaction);
    }

    @Override
    public void addExpense(BigDecimal amount, String category, String description) {
        Transaction transaction = transactionFactory.createExpense(amount, category, description);
        transactionRepository.create(transaction);
    }

    @Override
    public BigDecimal getTotalIncome() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalExpense() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
