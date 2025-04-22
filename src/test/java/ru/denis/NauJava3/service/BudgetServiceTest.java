package ru.denis.NauJava3.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.denis.NauJava3.dto.BudgetDto;
import ru.denis.NauJava3.entity.Budget;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.exception.ResourceNotFoundException;
import ru.denis.NauJava3.repository.BudgetRepository;
import ru.denis.NauJava3.repository.CategoryRepository;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.repository.UserRepository;
import ru.denis.NauJava3.service.impl.BudgetServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link BudgetServiceImpl}.
 * Проверяет функциональность сервиса бюджетов с использованием JUnit и Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User testUser;
    private Category testCategory;
    private Budget testBudget;
    private BudgetDto testBudgetDto;
    private Transaction testTransaction;

    /**
     * Настраивает тестовое окружение перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");

        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setName("Monthly groceries budget");
        testBudget.setAmountLimit(new BigDecimal("500.00"));
        testBudget.setPeriodStart(LocalDate.of(2023, 10, 1));
        testBudget.setPeriodEnd(LocalDate.of(2023, 10, 31));
        testBudget.setUser(testUser);
        testBudget.setCategory(testCategory);

        testBudgetDto = new BudgetDto();
        testBudgetDto.setName("Monthly groceries budget");
        testBudgetDto.setAmountLimit(new BigDecimal("500.00"));
        testBudgetDto.setPeriodStart(LocalDate.of(2023, 10, 1));
        testBudgetDto.setPeriodEnd(LocalDate.of(2023, 10, 31));
        testBudgetDto.setCategoryId(1L);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(new BigDecimal("150.00"));
        testTransaction.setUser(testUser);
        testTransaction.setCategory(testCategory);
        testTransaction.setDate(LocalDateTime.of(2023, 10, 15, 12, 0));
    }

    /**
     * Проверяет успешное создание бюджета.
     */
    @Test
    void createBudget_SuccessfulCreation() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        Budget result = budgetService.createBudget(testBudgetDto);

        assertNotNull(result);
        assertEquals(testBudget.getId(), result.getId());
        assertEquals(testBudget.getName(), result.getName());
        assertEquals(testBudget.getAmountLimit(), result.getAmountLimit());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    /**
     * Проверяет успешный поиск бюджета по ID.
     */
    @Test
    void getBudgetById_SuccessfulRetrieval() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        Budget result = budgetService.getBudgetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Monthly groceries budget", result.getName());
    }

    /**
     * Проверяет успешное получение всех бюджетов пользователя.
     */
    @Test
    void getCurrentUserBudgets_SuccessfulRetrieval() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUser(testUser)).thenReturn(List.of(testBudget));

        List<Budget> results = budgetService.getCurrentUserBudgets();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(1L, results.getFirst().getId());
    }

    /**
     * Проверяет, что метод возвращает false, когда расходы в пределах бюджета.
     */
    @Test
    void isBudgetExceeded_WithinBudget_ReturnsFalse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(transactionRepository.findByUserAndCategoryAndDateBetween(
                eq(testUser),
                eq(testCategory),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(testTransaction));

        boolean result = budgetService.isBudgetExceeded(1L);

        assertFalse(result);
    }

    /**
     * Проверяет, что метод возвращает true, когда расходы превышают бюджет.
     */
    @Test
    void isBudgetExceeded_OverBudget_ReturnsTrue() {
        Transaction largeTransaction = new Transaction();
        largeTransaction.setId(2L);
        largeTransaction.setAmount(new BigDecimal("600.00"));
        largeTransaction.setUser(testUser);
        largeTransaction.setCategory(testCategory);
        largeTransaction.setDate(LocalDateTime.of(2023, 10, 20, 12, 0));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(transactionRepository.findByUserAndCategoryAndDateBetween(
                eq(testUser),
                eq(testCategory),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(largeTransaction));

        boolean result = budgetService.isBudgetExceeded(1L);

        assertTrue(result);
    }

    /**
     * Проверяет, что при создании бюджета с несуществующей категорией выбрасывается исключение.
     */
    @Test
    void createBudget_CategoryNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.createBudget(testBudgetDto);
        });

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    /**
     * Проверяет, что при попытке получить несуществующий бюджет выбрасывается исключение.
     */
    @Test
    void getBudgetById_BudgetNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.getBudgetById(999L);
        });
    }

    /**
     * Проверяет, что при попытке получить бюджет другого пользователя выбрасывается исключение.
     */
    @Test
    void getBudgetById_AnotherUsersBudget_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");

        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(2L);
        otherUserBudget.setName("Other user's budget");
        otherUserBudget.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(2L)).thenReturn(Optional.of(otherUserBudget));

        assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.getBudgetById(2L);
        });
    }

    /**
     * Проверяет, что метод возвращает пустой список, если у пользователя нет бюджетов.
     */
    @Test
    void getCurrentUserBudgets_UserWithNoBudgets_ReturnsEmptyList() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        List<Budget> results = budgetService.getCurrentUserBudgets();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    /**
     * Проверяет, что при попытке проверить превышение несуществующего бюджета выбрасывается исключение.
     */
    @Test
    void isBudgetExceeded_BudgetNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.isBudgetExceeded(999L);
        });
    }

    /**
     * Проверяет, что метод возвращает false, когда нет транзакций в бюджетном периоде.
     */
    @Test
    void isBudgetExceeded_NoTransactions_ReturnsFalse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(transactionRepository.findByUserAndCategoryAndDateBetween(
                eq(testUser),
                eq(testCategory),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        boolean result = budgetService.isBudgetExceeded(1L);

        assertFalse(result);
    }

    /**
     * Проверяет, что все методы выбрасывают исключение, если пользователь не найден.
     */
    @Test
    void allMethods_UserNotFound_ThrowException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> budgetService.createBudget(testBudgetDto));
        assertThrows(ResourceNotFoundException.class, () -> budgetService.getBudgetById(1L));
        assertThrows(ResourceNotFoundException.class, () -> budgetService.getCurrentUserBudgets());
        assertThrows(ResourceNotFoundException.class, () -> budgetService.isBudgetExceeded(1L));
    }
}