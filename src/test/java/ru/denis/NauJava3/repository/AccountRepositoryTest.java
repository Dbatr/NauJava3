package ru.denis.NauJava3.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.denis.NauJava3.entity.Account;
import ru.denis.NauJava3.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для репозитория AccountRepository.
 * Проверяет функциональность операций с банковскими счетами в базе данных.
 */
@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Тестирует поиск счетов по email пользователя и диапазону баланса.
     * Проверяет корректность поиска одного счета в заданном диапазоне.
     */
    @Test
    void testFindUserAccountsByEmailAndBalanceRange() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);

        Account account = new Account();
        account.setName("Test Account");
        account.setBalance(new BigDecimal("1000.00"));
        account.setCurrency("RUB");
        account.setAccountType(Account.AccountType.CARD);
        account.setUser(user);
        accountRepository.save(account);

        List<Account> foundAccounts = accountRepository.findUserAccountsByEmailAndBalanceRange(
                "test@test.com",
                new BigDecimal("500.00"),
                new BigDecimal("1500.00")
        );

        assertFalse(foundAccounts.isEmpty());
        Account foundAccount = foundAccounts.getFirst();
        assertEquals(account.getId(), foundAccount.getId());
        assertEquals(account.getBalance(), foundAccount.getBalance());
    }

    /**
     * Тестирует поиск счетов для пользователя с несколькими счетами в разных диапазонах баланса.
     * Проверяет, что возвращаются только счета в указанном диапазоне баланса.
     */
    @Test
    void testMultipleAccountsWithinBalanceRange() {
        User user = new User();
        user.setUsername("multiAccountUser");
        user.setEmail("multi@test.com");
        user.setPassword("password123");
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);

        Account account1 = new Account();
        account1.setName("Low Balance Account");
        account1.setBalance(new BigDecimal("100.00"));
        account1.setCurrency("RUB");
        account1.setAccountType(Account.AccountType.CASH);
        account1.setUser(user);
        accountRepository.save(account1);

        Account account2 = new Account();
        account2.setName("Medium Balance Account");
        account2.setBalance(new BigDecimal("500.00"));
        account2.setCurrency("RUB");
        account2.setAccountType(Account.AccountType.CARD);
        account2.setUser(user);
        accountRepository.save(account2);

        Account account3 = new Account();
        account3.setName("High Balance Account");
        account3.setBalance(new BigDecimal("2000.00"));
        account3.setCurrency("RUB");
        account3.setAccountType(Account.AccountType.DEPOSIT);
        account3.setUser(user);
        accountRepository.save(account3);

        List<Account> foundAccounts = accountRepository.findUserAccountsByEmailAndBalanceRange(
                "multi@test.com",
                new BigDecimal("400.00"),
                new BigDecimal("1000.00")
        );

        assertEquals(1, foundAccounts.size(), "Should find exactly one account within the range");
        Account foundAccount = foundAccounts.getFirst();
        assertEquals("Medium Balance Account", foundAccount.getName());
        assertEquals(new BigDecimal("500.00"), foundAccount.getBalance());
    }
}
