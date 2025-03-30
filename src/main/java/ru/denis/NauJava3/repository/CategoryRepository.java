package ru.denis.NauJava3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.dao.CategoryRepositoryCustom;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.enums.OperationType;

import java.util.List;

/**
 * Репозиторий для работы с категориями.
 * Предоставляет методы для выполнения операций с сущностью {@link Category} в базе данных.
 * Расширяет функциональность с помощью пользовательских методов поиска.
 */
@RepositoryRestResource(path = "categories")
public interface CategoryRepository extends CrudRepository<Category, Long>,
                                            CategoryRepositoryCustom {
    /**
     * Поиск категорий по типу операции, цветовому коду и части названия.
     *
     * @param type тип операции (доход/расход)
     * @param colorCode цветовой код категории
     * @param namePart часть названия категории для поиска
     * @return список категорий, удовлетворяющих условиям поиска
     */
    List<Category> findByTypeAndColorCodeAndNameContaining(
            OperationType type,
            String colorCode,
            String namePart
    );
}
