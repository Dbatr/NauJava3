package ru.denis.NauJava3.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.enums.OperationType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки функциональности CategoryRepository.
 * Тесты проверяют операции поиска категорий по различным критериям.
 */
@SpringBootTest
@Transactional
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    /**
     * Тестирует поиск категории по типу, цветовому коду и части имени.
     * Проверяет корректность сохранения категории и последующего её поиска
     * с использованием всех параметров фильтрации.
     */
    @Test
    void testFindByTypeAndColorCodeAndNameContaining() {
        Category category = new Category();
        category.setName("Test Category");
        category.setType(OperationType.INCOME);
        category.setColorCode("#FF0000");
        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getId(), "Category should be saved with an ID");

        List<Category> foundCategories = categoryRepository.findByTypeAndColorCodeAndNameContaining(
                OperationType.INCOME,
                "#FF0000",
                "Test"
        );

        assertNotNull(foundCategories, "Found categories list should not be null");
        assertFalse(foundCategories.isEmpty(), "Found categories list should not be empty");

        Category foundCategory = foundCategories.getFirst();

        assertEquals(savedCategory.getId(), foundCategory.getId(),
                "Found category ID should match saved category ID");
        assertEquals(savedCategory.getName(), foundCategory.getName(),
                "Found category name should match saved category name");
        assertEquals(savedCategory.getType(), foundCategory.getType(),
                "Found category type should match saved category type");
        assertEquals(savedCategory.getColorCode(), foundCategory.getColorCode(),
                "Found category color code should match saved category color code");
    }

    /**
     * Тестирует поиск категории с параметрами, которые не должны дать результатов.
     * Проверяет корректность работы фильтрации при поиске категорий
     * с несоответствующими параметрами (неправильный тип операции).
     */
    @Test
    void testFindByTypeAndColorCodeAndNameContainingNoMatch() {
        Category category = new Category();
        category.setName("Test Category");
        category.setType(OperationType.INCOME);
        category.setColorCode("#FF0000");
        categoryRepository.save(category);

        List<Category> foundCategories = categoryRepository.findByTypeAndColorCodeAndNameContaining(
                OperationType.EXPENSE,
                "#FF0000",
                "Test"
        );

        assertTrue(foundCategories.isEmpty(), "Should not find categories with wrong type");
    }
}