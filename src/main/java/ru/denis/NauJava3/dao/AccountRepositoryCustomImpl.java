package ru.denis.NauJava3.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Account;
import ru.denis.NauJava3.entity.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация пользовательских методов поиска счетов с использованием Criteria API.
 * Предоставляет гибкие возможности построения динамических запросов к базе данных.
 */
@Repository
public class AccountRepositoryCustomImpl implements AccountRepositoryCustom {

    /** Менеджер сущностей для работы с базой данных */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     *
     * Метод строит динамический запрос с следующей логикой:
     * 1. Создает базовый запрос для сущности Account
     * 2. Выполняет соединение с таблицей пользователей
     * 3. Добавляет условие фильтрации по email пользователя (если указан)
     * 4. Добавляет условия фильтрации по диапазону баланса (если указаны)
     * 5. Комбинирует все условия через логическое И (AND)
     */
    @Override
    public List<Account> findUserAccountsByEmailAndBalanceRangeCriteria(
            String userEmail,
            BigDecimal minBalance,
            BigDecimal maxBalance) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);

        Root<Account> account = criteriaQuery.from(Account.class);

        Join<Account, User> userJoin = account.join("user");

        List<Predicate> predicates = new ArrayList<>();

        if (userEmail != null && !userEmail.isEmpty()) {
            predicates.add(criteriaBuilder.equal(userJoin.get("email"), userEmail));
        }

        if (minBalance != null && maxBalance != null) {
            predicates.add(criteriaBuilder.between(
                    account.get("balance"),
                    minBalance,
                    maxBalance
            ));
        } else if (minBalance != null) {
            predicates.add(criteriaBuilder.ge(account.get("balance"), minBalance));
        } else if (maxBalance != null) {
            predicates.add(criteriaBuilder.le(account.get("balance"), maxBalance));
        }

        if (!predicates.isEmpty()) {
            criteriaQuery.where(
                    criteriaBuilder.and(predicates.toArray(new Predicate[0]))
            );
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
