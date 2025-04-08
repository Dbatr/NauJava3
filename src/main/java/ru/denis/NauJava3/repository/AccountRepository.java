package ru.denis.NauJava3.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.dao.AccountRepositoryCustom;
import ru.denis.NauJava3.entity.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Репозиторий для работы с банковскими счетами.
 * Предоставляет методы для выполнения операций с сущностью Account в базе данных.
 */
@RepositoryRestResource(path = "accounts")
public interface AccountRepository extends CrudRepository<Account, Long>,
                                            AccountRepositoryCustom {

    /**
     * Поиск счетов пользователя по email и диапазону баланса.
     *
     * @param userEmail  email пользователя
     * @param minBalance минимальная сумма баланса
     * @param maxBalance максимальная сумма баланса
     * @return список счетов, удовлетворяющих условиям поиска
     */
    @Query("SELECT a FROM Account a WHERE a.user.email = :userEmail AND a.balance BETWEEN :minBalance AND :maxBalance")
    List<Account> findUserAccountsByEmailAndBalanceRange(
            @Param("userEmail") String userEmail,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance
    );
}
