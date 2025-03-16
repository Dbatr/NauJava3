package ru.denis.NauJava3.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.denis.NauJava3.config.AppConfig;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.factory.TransactionFactory;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.service.FinanceService;

import java.math.BigDecimal;

/**
 * Реализация сервиса управления финансами
 */
@Service
public class FinanceServiceImpl implements FinanceService {

    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;
    private final AppConfig appConfig;

    /**
     * @param transactionRepository репозиторий для работы с транзакциями
     * @param transactionFactory фабрика для создания транзакций
     * @param appConfig конфигурация приложения
     */
    @Autowired
    public FinanceServiceImpl(TransactionRepository transactionRepository,
                              TransactionFactory transactionFactory,
                              AppConfig appConfig) {
        this.transactionRepository = transactionRepository;
        this.transactionFactory = transactionFactory;
        this.appConfig = appConfig;
    }

    /**
     * Инициализация сервиса, вывод информации о приложении
     */
    @PostConstruct
    public void init() {
        System.out.println("=======================================");
        System.out.println("Application: " + appConfig.getAppName());
        System.out.println("Version: " + appConfig.getAppVersion());
        System.out.println("=======================================");
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
