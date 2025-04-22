package ru.denis.NauJava3.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import ru.denis.NauJava3.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с транзакциями.
 * Предоставляет базовые операции CRUD для сущности {@link Transaction}.
 */
@RepositoryRestResource(path = "transactions")
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByUserAndCategoryAndDateBetween(
            User user,
            Category category,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}