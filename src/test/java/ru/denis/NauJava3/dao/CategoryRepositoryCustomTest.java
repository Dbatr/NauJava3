package ru.denis.NauJava3.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.enums.OperationType;
import ru.denis.NauJava3.repository.CategoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для пользовательской реализации репозитория категорий.
 * Проверяет функциональность поиска категорий с использованием Criteria API.
 * Тесты охватывают различные сценарии поиска и комбинации параметров.
 */
@SpringBootTest
class CategoryRepositoryCustomTest {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Тестирует поиск категорий по всем критериям поиска.
     * Проверяет корректность работы метода при точном совпадении всех параметров:
     * - тип операции
     * - цветовой код
     * - часть названия
     */
    @Test
    void testFindByTypeAndColorCodeAndNameContainingCriteria() {
        Category category = new Category();
        category.setName("Test Category Criteria");
        category.setType(OperationType.EXPENSE);
        category.setColorCode("#00FF00");
        categoryRepository.save(category);

        List<Category> foundCategories = categoryRepository.findByTypeAndColorCodeAndNameContainingCriteria(
                OperationType.EXPENSE,
                "#00FF00",
                "Criteria"
        );

        assertFalse(foundCategories.isEmpty());
        Category foundCategory = foundCategories.getFirst();
        assertEquals(category.getId(), foundCategory.getId());
        assertEquals(category.getName(), foundCategory.getName());
    }

    /**
     * Тестирует поиск категорий с частичными критериями поиска.
     * Проверяет работу метода при:
     * - поиске только по типу операции
     * - поиске с null значениями параметров
     * - поиске нескольких категорий одного типа
     */
    @Test
    void testFindCategoriesWithPartialCriteria() {
        Category category1 = new Category();
        category1.setName("Income Category One");
        category1.setType(OperationType.INCOME);
        category1.setColorCode("#FF0000");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Income Category Two");
        category2.setType(OperationType.INCOME);
        category2.setColorCode("#00FF00");
        categoryRepository.save(category2);

        // Тест 1: Поиск только по типу операции
        List<Category> foundByType = categoryRepository.findByTypeAndColorCodeAndNameContainingCriteria(
                OperationType.INCOME,
                null,
                null
        );
        assertEquals(2, foundByType.size(), "Both categories with type INCOME must be found");

        // Тест 2: Поиск по типу и части названия
        List<Category> foundByTypeAndName = categoryRepository.findByTypeAndColorCodeAndNameContainingCriteria(
                OperationType.INCOME,
                null,
                "One"
        );
        assertEquals(1, foundByTypeAndName.size(), "There must be one category with a name containing 'One'");
        assertEquals("Income Category One", foundByTypeAndName.getFirst().getName());

        // Тест 3: Поиск по несуществующим критериям
        List<Category> notFound = categoryRepository.findByTypeAndColorCodeAndNameContainingCriteria(
                OperationType.EXPENSE,
                "#FFFFFF",
                "NonExistent"
        );
        assertTrue(notFound.isEmpty(), "No categories should be found based on non-existent criteria");
    }
}
