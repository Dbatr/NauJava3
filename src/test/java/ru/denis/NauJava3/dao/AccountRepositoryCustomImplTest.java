package ru.denis.NauJava3.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.NauJava3.entity.Account;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.repository.AccountRepository;
import ru.denis.NauJava3.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для пользовательской реализации репозитория счетов.
 * Проверяет функциональность поиска счетов с использованием Criteria API.
 */
@SpringBootTest
@Transactional
class AccountRepositoryCustomImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Тестирует поиск счетов по email пользователя и диапазону баланса.
     * Проверяет различные сценарии поиска:
     * 1. Поиск в конкретном диапазоне балансов
     * 2. Поиск счетов с балансом выше минимального
     * 3. Поиск счетов с балансом ниже максимального
     * 4. Поиск всех счетов пользователя
     */
    @Test
    void testFindUserAccountsByEmailAndBalanceRangeCriteria() {
        User user = createTestUser();

        createTestAccount(user, "Account 1", new BigDecimal("1000.00"));
        createTestAccount(user, "Account 2", new BigDecimal("2000.00"));
        createTestAccount(user, "Account 3", new BigDecimal("3000.00"));

        // Тест 1: Поиск счетов в диапазоне
        List<Account> accounts = accountRepository.findUserAccountsByEmailAndBalanceRangeCriteria(
                user.getEmail(),
                new BigDecimal("1500.00"),
                new BigDecimal("2500.00")
        );
        assertEquals(1, accounts.size());
        assertEquals(new BigDecimal("2000.00"), accounts.getFirst().getBalance());

        // Тест 2: Поиск счетов выше минимального баланса
        accounts = accountRepository.findUserAccountsByEmailAndBalanceRangeCriteria(
                user.getEmail(),
                new BigDecimal("2500.00"),
                null
        );
        assertEquals(1, accounts.size());
        assertEquals(new BigDecimal("3000.00"), accounts.getFirst().getBalance());

        // Тест 3: Поиск счетов ниже максимального баланса
        accounts = accountRepository.findUserAccountsByEmailAndBalanceRangeCriteria(
                user.getEmail(),
                null,
                new BigDecimal("1500.00")
        );
        assertEquals(1, accounts.size());
        assertEquals(new BigDecimal("1000.00"), accounts.getFirst().getBalance());

        // Тест 4: Поиск всех счетов пользователя
        accounts = accountRepository.findUserAccountsByEmailAndBalanceRangeCriteria(
                user.getEmail(),
                null,
                null
        );
        assertEquals(3, accounts.size());
    }

    /**
     * Тестирует поиск счетов для несуществующего email.
     * Проверяет, что метод корректно обрабатывает случай отсутствия данных.
     */
    @Test
    void testFindUserAccountsWithNonExistentEmail() {
        List<Account> accounts = accountRepository.findUserAccountsByEmailAndBalanceRangeCriteria(
                "nonexistent@email.com",
                new BigDecimal("1000.00"),
                new BigDecimal("2000.00")
        );
        assertTrue(accounts.isEmpty());
    }

    /**
     * Вспомогательный метод для создания тестового пользователя.
     * Создает пользователя с уникальными данными для избежания конфликтов.
     *
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
     * Вспомогательный метод для создания тестового счета.
     * Создает счет с указанными параметрами и привязывает его к пользователю.
     *
     * @param user владелец счета
     * @param name название счета
     * @param balance начальный баланс счета
     */
    private void createTestAccount(User user, String name, BigDecimal balance) {
        Account account = new Account();
        account.setName(name);
        account.setBalance(balance);
        account.setCurrency("RUB");
        account.setAccountType(Account.AccountType.CARD);
        account.setUser(user);
        accountRepository.save(account);
    }
}
