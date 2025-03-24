package ru.denis.NauJava3.dao;

import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.enums.OperationType;

import java.util.List;

/**
 * Интерфейс для реализации пользовательских методов поиска категорий.
 * Предоставляет расширенные возможности поиска с использованием Criteria API.
 */
public interface CategoryRepositoryCustom {
    /**
     * Поиск категорий по типу, цветовому коду и части имени с использованием Criteria API
     * @param type тип операции
     * @param colorCode цветовой код
     * @param namePart часть имени категории
     * @return список найденных категорий
     */
    List<Category> findByTypeAndColorCodeAndNameContainingCriteria(
            OperationType type,
            String colorCode,
            String namePart
    );
}
