package ru.denis.NauJava3.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.enums.OperationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация пользовательских методов поиска категорий с использованием Criteria API.
 * Обеспечивает гибкий поиск категорий с динамическим построением условий запроса.
 */
@Repository
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    /** Менеджер сущностей для работы с базой данных */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     *
     * Метод строит динамический запрос с следующей логикой:
     * 1. Создает базовый запрос для сущности Category
     * 2. Добавляет условие для типа операции (если указан)
     * 3. Добавляет условие для цветового кода (если указан)
     * 4. Добавляет условие частичного совпадения названия (если указано)
     * 5. Комбинирует все условия через логическое И (AND)
     */
    @Override
    public List<Category> findByTypeAndColorCodeAndNameContainingCriteria(
            OperationType type,
            String colorCode,
            String namePart) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);

        Root<Category> category = criteriaQuery.from(Category.class);

        List<Predicate> predicates = new ArrayList<>();

        if (type != null) {
            predicates.add(criteriaBuilder.equal(category.get("type"), type));
        }

        if (colorCode != null && !colorCode.isEmpty()) {
            predicates.add(criteriaBuilder.equal(category.get("colorCode"), colorCode));
        }

        if (namePart != null && !namePart.isEmpty()) {
            predicates.add(criteriaBuilder.like(
                    category.get("name"),
                    "%" + namePart + "%"
            ));
        }

        if (!predicates.isEmpty()) {
            criteriaQuery.where(
                    criteriaBuilder.and(predicates.toArray(new Predicate[0]))
            );
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
