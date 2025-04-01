package ru.denis.NauJava3.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.NauJava3.entity.Account;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.entity.enums.OperationType;
import ru.denis.NauJava3.repository.AccountRepository;
import ru.denis.NauJava3.repository.CategoryRepository;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для TransactionService.
 * Проверяет функциональность создания и удаления транзакций, включая:
 * - Создание транзакций доходов и расходов
 * - Проверку баланса счета после операций
 * - Обработку ошибочных ситуаций
 * - Удаление транзакций и восстановление баланса
 */
@SpringBootTest
@Transactional
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Проверяет успешное создание транзакции дохода
     * и корректное обновление баланса счета.
     */
    @Test
    void testCreateTransactionSuccess() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        Category category = createTestCategory(OperationType.INCOME);

        BigDecimal initialBalance = account.getBalance();
        BigDecimal transactionAmount = new BigDecimal("100.00");

        Transaction createdTransaction = transactionService.createTransaction(
                account.getId(),
                category.getId(),
                transactionAmount,
                "Test transaction",
                OperationType.INCOME
        );

        assertNotNull(createdTransaction, "Transaction should be created");
        assertNotNull(createdTransaction.getId(), "Transaction should have an ID");
        assertEquals(transactionAmount, createdTransaction.getAmount(), "Transaction amount should match");
        assertEquals("Test transaction", createdTransaction.getDescription(), "Transaction description should match");
        assertEquals(OperationType.INCOME, createdTransaction.getType(), "Transaction type should match");
        assertEquals(account.getId(), createdTransaction.getAccount().getId(), "Transaction account should match");
        assertEquals(category.getId(), createdTransaction.getCategory().getId(), "Transaction category should match");
        assertEquals(user.getId(), createdTransaction.getUser().getId(), "Transaction user should match");

        Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(initialBalance.add(transactionAmount), updatedAccount.getBalance(),
                "Account balance should be increased by transaction amount");
    }

    /**
     * Проверяет обработку ошибки при попытке создания расходной транзакции
     * с суммой, превышающей баланс счета.
     */
    @Test
    void testCreateTransactionFailure() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        Category category = createTestCategory(OperationType.EXPENSE);

        BigDecimal initialBalance = account.getBalance();
        BigDecimal tooLargeAmount = initialBalance.add(new BigDecimal("1000.00"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(
                    account.getId(),
                    category.getId(),
                    tooLargeAmount,
                    "Test transaction",
                    OperationType.EXPENSE
            );
        }, "Should throw RuntimeException when insufficient funds");

        assertTrue(exception.getMessage().contains("Недостаточно средств на счете"),
                "Exception message should indicate insufficient funds");

        Account unchangedAccount = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(initialBalance, unchangedAccount.getBalance(),
                "Account balance should remain unchanged");

        assertFalse(transactionRepository.findAll().iterator().hasNext(),
                "No transaction should be created when operation fails");
    }

    /**
     * Проверяет обработку ошибки при несоответствии типа операции
     * и типа категории транзакции.
     */
    @Test
    void testCreateTransactionWithMismatchedTypes() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        Category expenseCategory = createTestCategory(OperationType.EXPENSE);

        BigDecimal amount = new BigDecimal("100.00");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(
                    account.getId(),
                    expenseCategory.getId(),
                    amount,
                    "Test transaction",
                    OperationType.INCOME
            );
        }, "Should throw RuntimeException when operation type doesn't match category type");

        assertTrue(exception.getMessage().contains("Тип операции не соответствует категории"),
                "Exception message should indicate type mismatch");

        assertFalse(transactionRepository.findAll().iterator().hasNext(),
                "No transaction should be created when operation fails");
    }

    /**
     * Проверяет обработку ошибки при попытке создания транзакции
     * с несуществующим счетом.
     */
    @Test
    void testCreateTransactionWithNonExistentAccount() {
        Category category = createTestCategory(OperationType.INCOME);
        BigDecimal amount = new BigDecimal("100.00");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(
                    999L,
                    category.getId(),
                    amount,
                    "Test transaction",
                    OperationType.INCOME
            );
        }, "Should throw RuntimeException when account doesn't exist");

        assertTrue(exception.getMessage().contains("Счет не найден"),
                "Exception message should indicate account not found");
    }

    /**
     * Проверяет обработку ошибки при попытке создания транзакции
     * с несуществующей категорией.
     */
    @Test
    void testCreateTransactionWithNonExistentCategory() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        BigDecimal amount = new BigDecimal("100.00");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(
                    account.getId(),
                    999L,
                    amount,
                    "Test transaction",
                    OperationType.INCOME
            );
        }, "Should throw RuntimeException when category doesn't exist");

        assertTrue(exception.getMessage().contains("Категория не найдена"),
                "Exception message should indicate category not found");
    }

    /**
     * Проверяет создание нескольких транзакций разных типов
     * и корректность итогового баланса счета.
     */
    @Test
    void testCreateMultipleTransactions() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        Category incomeCategory = createTestCategory(OperationType.INCOME);
        Category expenseCategory = createTestCategory(OperationType.EXPENSE);

        BigDecimal initialBalance = account.getBalance();
        BigDecimal incomeAmount = new BigDecimal("500.00");
        BigDecimal expenseAmount = new BigDecimal("300.00");

        Transaction incomeTransaction = transactionService.createTransaction(
                account.getId(),
                incomeCategory.getId(),
                incomeAmount,
                "Income transaction",
                OperationType.INCOME
        );

        Transaction expenseTransaction = transactionService.createTransaction(
                account.getId(),
                expenseCategory.getId(),
                expenseAmount,
                "Expense transaction",
                OperationType.EXPENSE
        );

        Account finalAccount = accountRepository.findById(account.getId()).orElseThrow();
        BigDecimal expectedBalance = initialBalance.add(incomeAmount).subtract(expenseAmount);
        assertEquals(expectedBalance, finalAccount.getBalance(),
                "Final balance should reflect both transactions");

        Iterable<Transaction> transactions = transactionRepository.findAll();
        List<Transaction> transactionList = new ArrayList<>();
        transactions.forEach(transactionList::add);

        assertEquals(2, transactionList.size(), "Should have created two transactions");
        assertTrue(transactionList.contains(incomeTransaction), "Income transaction should exist");
        assertTrue(transactionList.contains(expenseTransaction), "Expense transaction should exist");
    }

    /**
     * Проверяет успешное удаление транзакции дохода
     * и восстановление первоначального баланса счета.
     */
    @Test
    void testDeleteTransactionSuccess() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        Category category = createTestCategory(OperationType.INCOME);
        BigDecimal transactionAmount = new BigDecimal("100.00");
        BigDecimal initialBalance = account.getBalance();

        Transaction transaction = transactionService.createTransaction(
                account.getId(),
                category.getId(),
                transactionAmount,
                "Test transaction",
                OperationType.INCOME
        );

        Account accountAfterCreation = accountRepository.findById(account.getId()).orElseThrow();
        BigDecimal balanceAfterCreation = accountAfterCreation.getBalance();
        assertEquals(initialBalance.add(transactionAmount), balanceAfterCreation,
                "Balance should be increased after transaction creation");

        transactionService.deleteTransaction(transaction.getId());

        assertFalse(transactionRepository.findById(transaction.getId()).isPresent(),
                "Transaction should be deleted");

        Account accountAfterDeletion = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(initialBalance, accountAfterDeletion.getBalance(),
                "Balance should be restored after transaction deletion");
    }

    /**
     * Проверяет обработку ошибки при попытке удаления
     * несуществующей транзакции.
     */
    @Test
    void testDeleteTransactionWithNonExistentId() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.deleteTransaction(999L);
        }, "Should throw RuntimeException when transaction doesn't exist");

        assertTrue(exception.getMessage().contains("Транзакция не найдена"),
                "Exception message should indicate transaction not found");
    }

    /**
     * Проверяет успешное удаление транзакции расхода
     * и восстановление первоначального баланса счета.
     */
    @Test
    void testDeleteExpenseTransaction() {
        User user = createTestUser();
        Account account = createTestAccount(user);
        Category category = createTestCategory(OperationType.EXPENSE);
        BigDecimal transactionAmount = new BigDecimal("100.00");
        BigDecimal initialBalance = account.getBalance();

        Transaction transaction = transactionService.createTransaction(
                account.getId(),
                category.getId(),
                transactionAmount,
                "Test expense",
                OperationType.EXPENSE
        );

        Account accountAfterExpense = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(initialBalance.subtract(transactionAmount), accountAfterExpense.getBalance(),
                "Balance should be decreased after expense");

        transactionService.deleteTransaction(transaction.getId());

        Account accountAfterDeletion = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(initialBalance, accountAfterDeletion.getBalance(),
                "Balance should be restored after expense deletion");
    }

    /**
     * Создает тестового пользователя с уникальными данными.
     * @return сохраненный в базе данных пользователь
     */
    private User createTestUser() {
        String uniqueId = UUID.randomUUID().toString();
        User user = new User();
        user.setUsername("testUser_" + uniqueId);
        user.setEmail("test_" + uniqueId + "@test.com");
        user.setPassword("password");
        user.setRegistrationDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Создает тестовый счет для указанного пользователя.
     * @param user пользователь, которому принадлежит счет
     * @return сохраненный в базе данных счет
     */
    private Account createTestAccount(User user) {
        Account account = new Account();
        account.setName("Test Account");
        account.setBalance(new BigDecimal("1000.00"));
        account.setCurrency("RUB");
        account.setAccountType(Account.AccountType.CARD);
        account.setUser(user);
        return accountRepository.save(account);
    }

    /**
     * Создает тестовую категорию указанного типа.
     * @param type тип операции (доход/расход)
     * @return сохраненная в базе данных категория
     */
    private Category createTestCategory(OperationType type) {
        Category category = new Category();
        category.setName("Test Category");
        category.setType(type);
        category.setColorCode("#FF0000");
        return categoryRepository.save(category);
    }
}